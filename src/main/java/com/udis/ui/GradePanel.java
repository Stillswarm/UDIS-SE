package com.udis.ui;

import com.udis.dao.GradeDao;
import com.udis.dao.RegistrationDao;
import com.udis.dao.StudentDao;
import com.udis.model.Grade;
import com.udis.model.Registration;
import com.udis.model.Student;
import com.udis.service.AuditService;
import com.udis.service.AuthService;
import com.udis.service.GpaService;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.print.PrinterException;
import java.util.List;
import java.util.Map;

public class GradePanel extends JPanel {

    private final StudentDao studentDao = new StudentDao();
    private final RegistrationDao regDao = new RegistrationDao();
    private final GradeDao gradeDao = new GradeDao();
    private final GpaService gpaService = new GpaService();

    private final JTextField rollField = new JTextField(10);
    private final JTextField semesterField = new JTextField(4);
    private final JTextField yearField = new JTextField(6);
    private final JButton loadBtn = new JButton("Load Registrations");
    private final JButton saveBtn = new JButton("Save Grades");
    private final JButton printBtn = new JButton("Print Grade Sheet");

    private final JLabel statsLabel = new JLabel(" ");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Course", "Letter Grade"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return c == 1; }
    };
    private final JTable table = new JTable(model);

    public GradePanel() {
        setLayout(new BorderLayout(8, 8));
        UiUtils.padded(this);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createTitledBorder("Grade Entry"));
        top.add(new JLabel("Roll No:")); top.add(rollField);
        top.add(new JLabel("Semester:")); top.add(semesterField);
        top.add(new JLabel("Year:")); top.add(yearField);
        top.add(loadBtn);
        add(top, BorderLayout.NORTH);

        String[] letters = GpaService.LETTER_TO_POINTS.keySet().toArray(new String[0]);
        JComboBox<String> combo = new JComboBox<>(letters);
        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(combo));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(statsLabel, BorderLayout.WEST);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(saveBtn);
        btns.add(printBtn);
        bottom.add(btns, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        boolean canWrite = AuthService.canWrite(AuthService.Module.GRADE);
        saveBtn.setEnabled(canWrite);

        loadBtn.addActionListener(e -> loadRegs());
        saveBtn.addActionListener(e -> saveGrades());
        printBtn.addActionListener(e -> printSheet());
    }

    private void loadRegs() {
        model.setRowCount(0);
        statsLabel.setText(" ");
        try {
            String roll = rollField.getText().trim();
            int sem = UiUtils.parseInt(semesterField.getText(), "Semester");
            int year = UiUtils.parseInt(yearField.getText(), "Year");

            Student s = studentDao.findByRollNo(roll);
            if (s == null) { UiUtils.error(this, "Student not found."); return; }

            List<Registration> regs = regDao.findBySemester(roll, sem, year);
            if (regs.isEmpty()) { UiUtils.info(this, "No registrations for this semester."); return; }

            for (Registration r : regs) {
                Grade existing = gradeDao.find(roll, r.getCourseId(), sem, year);
                model.addRow(new Object[]{r.getCourseId(), existing == null ? "" : existing.getLetterGrade()});
            }
        } catch (IllegalArgumentException ex) { UiUtils.error(this, ex.getMessage()); }
    }

    private void saveGrades() {
        String roll = rollField.getText().trim();
        int sem, year;
        try {
            sem = UiUtils.parseInt(semesterField.getText(), "Semester");
            year = UiUtils.parseInt(yearField.getText(), "Year");
        } catch (IllegalArgumentException ex) { UiUtils.error(this, ex.getMessage()); return; }
        if (!UiUtils.confirm(this, "Save grades? Final grades will update GPA/CGPA.")) return;

        int saved = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            String courseId = (String) model.getValueAt(i, 0);
            Object letterObj = model.getValueAt(i, 1);
            if (letterObj == null || letterObj.toString().isBlank()) continue;
            String letter = letterObj.toString().trim();
            try {
                double points = gpaService.pointsFor(letter);
                Grade g = new Grade();
                g.setRollNo(roll); g.setCourseId(courseId); g.setSemester(sem); g.setYear(year);
                g.setLetterGrade(letter); g.setGradePoints(points);
                gradeDao.upsert(g);

                // Mark registration as COMPLETED / BACKLOG
                Registration match = null;
                for (Registration r : regDao.findBySemester(roll, sem, year)) {
                    if (r.getCourseId().equals(courseId)) { match = r; break; }
                }
                if (match != null) {
                    regDao.updateStatus(match.getRegId(), "F".equals(letter) ? "BACKLOG" : "COMPLETED");
                }
                saved++;
            } catch (IllegalArgumentException ex) { UiUtils.error(this, ex.getMessage()); }
        }
        AuditService.log("GRADE_ENTRY", "grade:" + roll + "/" + sem + "/" + year + "/" + saved);
        updateStats(roll, sem, year);
        UiUtils.info(this, "Saved " + saved + " grade(s).");
    }

    private void updateStats(String roll, int sem, int year) {
        double gpa = gpaService.semesterGpa(roll, sem, year);
        double cgpa = gpaService.cgpa(roll);
        statsLabel.setText(String.format("  Semester GPA: %.2f    |    CGPA: %.2f", gpa, cgpa));
    }

    private void printSheet() {
        String roll = rollField.getText().trim();
        if (roll.isEmpty()) { UiUtils.error(this, "Load a student first."); return; }
        int sem, year;
        try {
            sem = UiUtils.parseInt(semesterField.getText(), "Semester");
            year = UiUtils.parseInt(yearField.getText(), "Year");
        } catch (IllegalArgumentException ex) { UiUtils.error(this, ex.getMessage()); return; }

        Student s = studentDao.findByRollNo(roll);
        if (s == null) { UiUtils.error(this, "Student not found."); return; }

        List<Grade> semGrades = gradeDao.findBySemester(roll, sem, year);
        double gpa = gpaService.gpa(semGrades);
        double cgpa = gpaService.cgpa(roll);

        StringBuilder rows = new StringBuilder();
        for (Grade g : semGrades) {
            rows.append("<tr><td>").append(g.getCourseId()).append("</td>")
                .append("<td>").append(g.getCredits()).append("</td>")
                .append("<td>").append(g.getLetterGrade()).append("</td>")
                .append("<td>").append(String.format("%.2f", g.getGradePoints())).append("</td></tr>");
        }

        StringBuilder history = new StringBuilder();
        Map<String, Double> semHist = gpaService.semesterHistory(roll);
        for (Map.Entry<String, Double> e : semHist.entrySet()) {
            history.append("<tr><td>").append(e.getKey()).append("</td>")
                   .append("<td>").append(String.format("%.2f", e.getValue())).append("</td></tr>");
        }

        String html = "<html><body style='font-family:sans-serif;'>"
                + "<h2 style='text-align:center;margin-bottom:0;'>IIIT Ranchi &mdash; Department of CSE</h2>"
                + "<h3 style='text-align:center;margin-top:2px;'>Semester Grade Sheet</h3>"
                + "<hr/>"
                + "<p><b>Name:</b> " + s.getName() + "<br/>"
                + "<b>Roll No:</b> " + s.getRollNo() + "<br/>"
                + "<b>Program:</b> " + nz(s.getProgram()) + " &nbsp; <b>Batch:</b> " + nz(s.getBatch()) + "<br/>"
                + "<b>Semester:</b> " + sem + " / " + year + "</p>"
                + "<table border='1' cellspacing='0' cellpadding='6'>"
                + "<tr><th>Course</th><th>Credits</th><th>Letter</th><th>Points</th></tr>"
                + rows + "</table>"
                + "<p><b>Semester GPA:</b> " + String.format("%.2f", gpa)
                + " &nbsp; &nbsp; <b>CGPA:</b> " + String.format("%.2f", cgpa) + "</p>"
                + "<h4>Semester-wise GPA History</h4>"
                + "<table border='1' cellspacing='0' cellpadding='4'>"
                + "<tr><th>Semester</th><th>GPA</th></tr>"
                + history + "</table>"
                + "<p style='margin-top:30px;font-size:10px;color:#666;'>"
                + "Generated by UDIS. This is a computer-generated document.</p>"
                + "</body></html>";

        JEditorPane pane = new JEditorPane("text/html", html);
        pane.setEditable(false);
        pane.setCaretPosition(0);

        JDialog dlg = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                "Grade Sheet - " + s.getName(), true);
        dlg.setLayout(new BorderLayout(8, 8));
        dlg.add(new JScrollPane(pane), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton printNow = new JButton("Print...");
        JButton close = new JButton("Close");
        btns.add(printNow); btns.add(close);
        dlg.add(btns, BorderLayout.SOUTH);

        printNow.addActionListener(ev -> {
            try {
                if (pane.print()) {
                    AuditService.log("PRINT", "grade_sheet:" + roll + "/" + sem + "/" + year);
                }
            } catch (PrinterException ex) {
                UiUtils.error(dlg, "Printing failed: " + ex.getMessage());
            }
        });
        close.addActionListener(ev -> dlg.dispose());

        dlg.setSize(new Dimension(680, 720));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private String nz(String s) { return s == null ? "-" : s; }
}
