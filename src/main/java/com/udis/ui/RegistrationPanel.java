package com.udis.ui;

import com.udis.dao.CourseDao;
import com.udis.dao.RegistrationDao;
import com.udis.dao.StudentDao;
import com.udis.model.Course;
import com.udis.model.Registration;
import com.udis.model.Student;
import com.udis.service.AuthService;
import com.udis.service.RegistrationService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

public class RegistrationPanel extends JPanel {

    private final StudentDao studentDao = new StudentDao();
    private final CourseDao courseDao = new CourseDao();
    private final RegistrationDao regDao = new RegistrationDao();
    private final RegistrationService service = new RegistrationService();

    private final JTextField rollField = new JTextField(14);
    private final JTextField semesterField = new JTextField(4);
    private final JTextField yearField = new JTextField(6);
    private final JButton loadBtn = new JButton("Load Profile");
    private final JButton registerBtn = new JButton("Confirm Registration");

    private final JLabel profileLabel = new JLabel(" ");
    private final DefaultListModel<String> completedModel = new DefaultListModel<>();
    private final DefaultListModel<String> backlogModel = new DefaultListModel<>();
    private final DefaultListModel<String> availableModel = new DefaultListModel<>();
    private final JList<String> completedList = new JList<>(completedModel);
    private final JList<String> backlogList = new JList<>(backlogModel);
    private final JList<String> availableList = new JList<>(availableModel);

    public RegistrationPanel() {
        setLayout(new BorderLayout(8, 8));
        UiUtils.padded(this);
        add(buildTop(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);

        availableList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        boolean canWrite = AuthService.canWrite(AuthService.Module.REGISTRATION);
        registerBtn.setEnabled(canWrite);

        loadBtn.addActionListener(e -> loadProfile());
        registerBtn.addActionListener(e -> confirm());
    }

    private JPanel buildTop() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createTitledBorder("Semester Registration"));
        top.add(new JLabel("Roll No:"));       top.add(rollField);
        top.add(new JLabel("Semester:"));      top.add(semesterField);
        top.add(new JLabel("Year:"));          top.add(yearField);
        top.add(loadBtn);
        return top;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(8, 8));

        profileLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        profileLabel.setFont(profileLabel.getFont().deriveFont(Font.PLAIN, 13f));
        center.add(profileLabel, BorderLayout.NORTH);

        JPanel lists = new JPanel(new GridLayout(1, 3, 8, 8));
        lists.add(wrapList("Completed", completedList));
        lists.add(wrapList("Back-log", backlogList));
        lists.add(wrapList("Available to Register (multi-select)", availableList));
        center.add(lists, BorderLayout.CENTER);
        return center;
    }

    private JPanel wrapList(String title, JList<String> list) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        JScrollPane sp = new JScrollPane(list);
        sp.setPreferredSize(new Dimension(260, 220));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildBottom() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(registerBtn);
        return bottom;
    }

    private void loadProfile() {
        try {
            String roll = rollField.getText().trim();
            if (roll.isEmpty()) { UiUtils.error(this, "Enter roll number."); return; }
            Student s = studentDao.findByRollNo(roll);
            if (s == null) { UiUtils.error(this, "No student with roll no '" + roll + "'."); return; }

            profileLabel.setText("<html><b>" + s.getName() + "</b> &nbsp; Roll: " + s.getRollNo()
                    + " &nbsp; Program: " + nz(s.getProgram()) + " &nbsp; Batch: " + nz(s.getBatch()) + "</html>");

            completedModel.clear();
            backlogModel.clear();
            availableModel.clear();

            List<Registration> regs = regDao.findByStudent(roll);
            for (Registration r : regs) {
                String line = r.getCourseId() + " (Sem " + r.getSemester() + "/" + r.getYear() + ") [" + r.getStatus() + "]";
                if ("COMPLETED".equals(r.getStatus())) completedModel.addElement(line);
                else if ("BACKLOG".equals(r.getStatus())) backlogModel.addElement(line);
            }

            List<Course> all = courseDao.findAll();
            for (Course c : all) {
                availableModel.addElement(c.getCourseId() + " - " + c.getCourseName()
                        + " (" + c.getCredits() + "cr, Sem " + c.getSemester()
                        + (c.getPrerequisiteId() == null ? "" : ", needs " + c.getPrerequisiteId()) + ")");
            }
        } catch (RuntimeException ex) {
            UiUtils.error(this, ex.getMessage());
        }
    }

    private void confirm() {
        String roll = rollField.getText().trim();
        if (roll.isEmpty()) { UiUtils.error(this, "Load a student profile first."); return; }
        int semester, year;
        try {
            semester = UiUtils.parseInt(semesterField.getText(), "Semester");
            year = UiUtils.parseInt(yearField.getText(), "Year");
        } catch (IllegalArgumentException ex) { UiUtils.error(this, ex.getMessage()); return; }

        List<String> selected = availableList.getSelectedValuesList();
        if (selected.isEmpty()) { UiUtils.error(this, "Select at least one course to register."); return; }

        StringBuilder ok = new StringBuilder();
        StringBuilder fail = new StringBuilder();
        for (String entry : selected) {
            String courseId = entry.split(" ")[0];
            try {
                service.register(roll, courseId, semester, year);
                ok.append("\u2713 ").append(courseId).append('\n');
            } catch (RegistrationService.RegistrationException ex) {
                fail.append("\u2717 ").append(courseId).append(": ").append(ex.getMessage()).append('\n');
            }
        }

        String msg = (ok.length() > 0 ? "Registered:\n" + ok : "")
                   + (fail.length() > 0 ? "\nFailed:\n" + fail : "");
        UiUtils.info(this, msg.isBlank() ? "No changes." : msg);
        loadProfile();
    }

    private String nz(String s) { return s == null ? "-" : s; }
}
