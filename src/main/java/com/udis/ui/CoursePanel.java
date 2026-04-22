package com.udis.ui;

import com.udis.dao.CourseDao;
import com.udis.model.Course;
import com.udis.service.AuditService;
import com.udis.service.AuthService;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.List;

public class CoursePanel extends JPanel {

    private final CourseDao dao = new CourseDao();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Course ID", "Name", "Credits", "Semester", "Prerequisite"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    private final JTextField idField = new JTextField(10);
    private final JTextField nameField = new JTextField(22);
    private final JTextField creditsField = new JTextField(4);
    private final JTextField semesterField = new JTextField(4);
    private final JComboBox<String> prereqBox = new JComboBox<>();

    private final JButton addBtn = new JButton("Add");
    private final JButton updateBtn = new JButton("Update");
    private final JButton deleteBtn = new JButton("Delete");
    private final JButton clearBtn = new JButton("Clear");

    public CoursePanel() {
        setLayout(new BorderLayout(8, 8));
        UiUtils.padded(this);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) loadSelection(); });
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.SOUTH);

        boolean canWrite = AuthService.canWrite(AuthService.Module.COURSE);
        addBtn.setEnabled(canWrite);
        updateBtn.setEnabled(canWrite);
        deleteBtn.setEnabled(canWrite);

        addBtn.addActionListener(e -> onAdd());
        updateBtn.addActionListener(e -> onUpdate());
        deleteBtn.addActionListener(e -> onDelete());
        clearBtn.addActionListener(e -> clearForm());

        refresh();
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Course"));
        form.add(new JLabel("Course ID*"), UiUtils.gbc(0, 0)); form.add(idField,       UiUtils.gbc(1, 0));
        form.add(new JLabel("Name*"),      UiUtils.gbc(2, 0)); form.add(nameField,     UiUtils.gbc(3, 0));
        form.add(new JLabel("Credits*"),   UiUtils.gbc(0, 1)); form.add(creditsField,  UiUtils.gbc(1, 1));
        form.add(new JLabel("Semester*"),  UiUtils.gbc(2, 1)); form.add(semesterField, UiUtils.gbc(3, 1));
        form.add(new JLabel("Prerequisite"), UiUtils.gbc(0, 2)); form.add(prereqBox,   UiUtils.gbc(1, 2));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(addBtn); buttons.add(updateBtn); buttons.add(deleteBtn); buttons.add(clearBtn);
        JPanel container = new JPanel(new BorderLayout());
        container.add(form, BorderLayout.CENTER);
        container.add(buttons, BorderLayout.SOUTH);
        return container;
    }

    private void refresh() {
        tableModel.setRowCount(0);
        List<Course> courses = dao.findAll();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("");
        for (Course c : courses) {
            tableModel.addRow(new Object[]{c.getCourseId(), c.getCourseName(), c.getCredits(),
                    c.getSemester(), c.getPrerequisiteId() == null ? "" : c.getPrerequisiteId()});
            model.addElement(c.getCourseId());
        }
        prereqBox.setModel(model);
    }

    private Course fromForm() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        if (id.isEmpty() || name.isEmpty()) throw new IllegalArgumentException("Course ID and Name are mandatory.");
        int credits = UiUtils.parseInt(creditsField.getText(), "Credits");
        int sem = UiUtils.parseInt(semesterField.getText(), "Semester");
        String prereq = (String) prereqBox.getSelectedItem();
        return new Course(id, name, credits, sem, prereq == null || prereq.isBlank() ? null : prereq);
    }

    private void loadSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        idField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        creditsField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        semesterField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        Object pre = tableModel.getValueAt(row, 4);
        prereqBox.setSelectedItem(pre == null ? "" : pre.toString());
    }

    private void clearForm() {
        idField.setText(""); nameField.setText(""); creditsField.setText("");
        semesterField.setText(""); prereqBox.setSelectedIndex(0);
        table.clearSelection();
    }

    private void onAdd() {
        try {
            Course c = fromForm();
            if (dao.exists(c.getCourseId())) { UiUtils.error(this, "Course ID already exists."); return; }
            dao.insert(c);
            AuditService.log("INSERT", "course:" + c.getCourseId());
            refresh(); clearForm();
        } catch (IllegalArgumentException ex) { UiUtils.error(this, ex.getMessage()); }
    }

    private void onUpdate() {
        try {
            Course c = fromForm();
            if (!dao.exists(c.getCourseId())) { UiUtils.error(this, "Course does not exist."); return; }
            dao.update(c);
            AuditService.log("UPDATE", "course:" + c.getCourseId());
            refresh();
        } catch (IllegalArgumentException ex) { UiUtils.error(this, ex.getMessage()); }
    }

    private void onDelete() {
        String id = idField.getText().trim();
        if (id.isEmpty()) { UiUtils.error(this, "Select a course to delete."); return; }
        if (!UiUtils.confirm(this, "Delete course " + id + "?")) return;
        try {
            dao.delete(id);
            AuditService.log("DELETE", "course:" + id);
            refresh(); clearForm();
        } catch (RuntimeException ex) {
            UiUtils.error(this, "Cannot delete: course may be referenced by registrations or other courses.");
        }
    }
}
