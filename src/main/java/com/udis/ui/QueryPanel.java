package com.udis.ui;

import com.udis.dao.GradeDao;
import com.udis.dao.RegistrationDao;
import com.udis.dao.StudentDao;
import com.udis.model.Grade;
import com.udis.model.Registration;
import com.udis.model.Student;
import com.udis.service.AuditService;
import com.udis.service.GpaService;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

public class QueryPanel extends JPanel {

    private final StudentDao studentDao = new StudentDao();
    private final RegistrationDao regDao = new RegistrationDao();
    private final GradeDao gradeDao = new GradeDao();
    private final GpaService gpaService = new GpaService();

    private final JTextField rollField = new JTextField(14);
    private final JButton goBtn = new JButton("Query");

    private final JLabel profileLabel = new JLabel(" ");
    private final JLabel cgpaLabel = new JLabel(" ");

    private final DefaultTableModel gradesModel = new DefaultTableModel(
            new Object[]{"Semester", "Year", "Course", "Credits", "Letter", "Points"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel currentModel = new DefaultTableModel(
            new Object[]{"Course", "Semester", "Year", "Status"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel backlogModel = new DefaultTableModel(
            new Object[]{"Course", "Semester", "Year"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel historyModel = new DefaultTableModel(
            new Object[]{"Semester", "GPA"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public QueryPanel() {
        setLayout(new BorderLayout(8, 8));
        UiUtils.padded(this);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createTitledBorder("Student Information Query"));
        top.add(new JLabel("Roll No:"));
        top.add(rollField);
        top.add(goBtn);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        profileLabel.setFont(profileLabel.getFont().deriveFont(Font.PLAIN, 13f));
        profileLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        center.add(profileLabel);
        cgpaLabel.setFont(cgpaLabel.getFont().deriveFont(Font.BOLD, 14f));
        cgpaLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));
        center.add(cgpaLabel);

        JPanel grid = new JPanel(new GridLayout(2, 2, 8, 8));
        grid.add(wrapTable("Completed Courses & Grades", gradesModel));
        grid.add(wrapTable("Current Semester Registration", currentModel));
        grid.add(wrapTable("Back-logs", backlogModel));
        grid.add(wrapTable("Semester-wise GPA History", historyModel));
        center.add(grid);
        add(center, BorderLayout.CENTER);

        goBtn.addActionListener(e -> runQuery());
        getRootPaneHack();
    }

    private void getRootPaneHack() {
        rollField.addActionListener(e -> goBtn.doClick());
    }

    private JPanel wrapTable(String title, DefaultTableModel m) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.add(new JScrollPane(new JTable(m)), BorderLayout.CENTER);
        return p;
    }

    private void runQuery() {
        String roll = rollField.getText().trim();
        if (roll.isEmpty()) { UiUtils.error(this, "Enter a roll number."); return; }
        Student s = studentDao.findByRollNo(roll);
        if (s == null) { UiUtils.error(this, "No student with roll no '" + roll + "'."); return; }

        profileLabel.setText("<html><b>" + s.getName() + "</b> &nbsp; Roll: " + s.getRollNo()
                + " &nbsp; Program: " + nz(s.getProgram()) + " &nbsp; Batch: " + nz(s.getBatch())
                + " &nbsp; Contact: " + nz(s.getContact()) + "</html>");

        gradesModel.setRowCount(0);
        for (Grade g : gradeDao.findByStudent(roll)) {
            gradesModel.addRow(new Object[]{g.getSemester(), g.getYear(), g.getCourseId(),
                    g.getCredits(), g.getLetterGrade(), g.getGradePoints()});
        }

        currentModel.setRowCount(0);
        backlogModel.setRowCount(0);
        List<Registration> regs = regDao.findByStudent(roll);
        for (Registration r : regs) {
            if ("REGISTERED".equals(r.getStatus())) {
                currentModel.addRow(new Object[]{r.getCourseId(), r.getSemester(), r.getYear(), r.getStatus()});
            } else if ("BACKLOG".equals(r.getStatus())) {
                backlogModel.addRow(new Object[]{r.getCourseId(), r.getSemester(), r.getYear()});
            }
        }

        historyModel.setRowCount(0);
        Map<String, Double> hist = gpaService.semesterHistory(roll);
        for (Map.Entry<String, Double> e : hist.entrySet()) {
            historyModel.addRow(new Object[]{e.getKey(), String.format("%.2f", e.getValue())});
        }

        double cgpa = gpaService.cgpa(roll);
        cgpaLabel.setText(String.format("  Current CGPA: %.2f", cgpa));

        AuditService.log("QUERY", "student:" + roll);
    }

    private String nz(String s) { return s == null ? "-" : s; }
}
