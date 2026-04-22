package com.udis.ui;

import com.udis.dao.StudentDao;
import com.udis.model.Student;
import com.udis.service.AuditService;
import com.udis.service.AuthService;

import javax.swing.BorderFactory;
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

public class StudentPanel extends JPanel {

    private final StudentDao dao = new StudentDao();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Roll No", "Name", "DOB", "Gender", "Program", "Batch", "Contact", "Address"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    private final JTextField rollField = new JTextField(12);
    private final JTextField nameField = new JTextField(18);
    private final JTextField dobField = new JTextField(10);
    private final JComboBox<String> genderBox = new JComboBox<>(new String[]{"M", "F", "Other"});
    private final JTextField programField = new JTextField(12);
    private final JTextField batchField = new JTextField(6);
    private final JTextField contactField = new JTextField(12);
    private final JTextField addressField = new JTextField(20);

    private final JButton addBtn = new JButton("Add");
    private final JButton updateBtn = new JButton("Update");
    private final JButton deleteBtn = new JButton("Delete");
    private final JButton clearBtn = new JButton("Clear");

    public StudentPanel() {
        setLayout(new BorderLayout(8, 8));
        UiUtils.padded(this);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) loadSelection(); });
        add(new JScrollPane(table), BorderLayout.CENTER);

        add(buildForm(), BorderLayout.SOUTH);

        boolean canWrite = AuthService.canWrite(AuthService.Module.STUDENT);
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
        form.setBorder(BorderFactory.createTitledBorder("Student Profile"));
        form.add(new JLabel("Roll No*"),  UiUtils.gbc(0, 0)); form.add(rollField,    UiUtils.gbc(1, 0));
        form.add(new JLabel("Name*"),     UiUtils.gbc(2, 0)); form.add(nameField,    UiUtils.gbc(3, 0));
        form.add(new JLabel("DOB (YYYY-MM-DD)"), UiUtils.gbc(0, 1)); form.add(dobField, UiUtils.gbc(1, 1));
        form.add(new JLabel("Gender"),    UiUtils.gbc(2, 1)); form.add(genderBox,    UiUtils.gbc(3, 1));
        form.add(new JLabel("Program"),   UiUtils.gbc(0, 2)); form.add(programField, UiUtils.gbc(1, 2));
        form.add(new JLabel("Batch"),     UiUtils.gbc(2, 2)); form.add(batchField,   UiUtils.gbc(3, 2));
        form.add(new JLabel("Contact"),   UiUtils.gbc(0, 3)); form.add(contactField, UiUtils.gbc(1, 3));
        form.add(new JLabel("Address"),   UiUtils.gbc(2, 3)); form.add(addressField, UiUtils.gbc(3, 3));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(clearBtn);

        JPanel container = new JPanel(new BorderLayout());
        container.add(form, BorderLayout.CENTER);
        container.add(buttons, BorderLayout.SOUTH);
        return container;
    }

    private void refresh() {
        tableModel.setRowCount(0);
        List<Student> students = dao.findAll();
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                    s.getRollNo(), s.getName(), s.getDob(), s.getGender(),
                    s.getProgram(), s.getBatch(), s.getContact(), s.getAddress()});
        }
    }

    private Student fromForm() {
        String roll = rollField.getText().trim();
        String name = nameField.getText().trim();
        if (roll.isEmpty() || name.isEmpty()) {
            throw new IllegalArgumentException("Roll No and Name are mandatory.");
        }
        Student s = new Student();
        s.setRollNo(roll);
        s.setName(name);
        s.setDob(UiUtils.parseDate(dobField.getText()));
        s.setGender((String) genderBox.getSelectedItem());
        s.setProgram(programField.getText().trim());
        s.setBatch(batchField.getText().trim());
        s.setContact(contactField.getText().trim());
        s.setAddress(addressField.getText().trim());
        return s;
    }

    private void loadSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        rollField.setText((String) tableModel.getValueAt(row, 0));
        nameField.setText((String) tableModel.getValueAt(row, 1));
        Object dob = tableModel.getValueAt(row, 2);
        dobField.setText(dob == null ? "" : dob.toString());
        genderBox.setSelectedItem(tableModel.getValueAt(row, 3));
        programField.setText(str(tableModel.getValueAt(row, 4)));
        batchField.setText(str(tableModel.getValueAt(row, 5)));
        contactField.setText(str(tableModel.getValueAt(row, 6)));
        addressField.setText(str(tableModel.getValueAt(row, 7)));
    }

    private String str(Object o) { return o == null ? "" : o.toString(); }

    private void clearForm() {
        rollField.setText(""); nameField.setText(""); dobField.setText("");
        genderBox.setSelectedIndex(0); programField.setText(""); batchField.setText("");
        contactField.setText(""); addressField.setText("");
        table.clearSelection();
    }

    private void onAdd() {
        try {
            Student s = fromForm();
            if (dao.exists(s.getRollNo())) {
                UiUtils.error(this, "A student with roll number '" + s.getRollNo() + "' already exists.");
                return;
            }
            dao.insert(s);
            AuditService.log("INSERT", "student:" + s.getRollNo());
            UiUtils.info(this, "Student added.");
            refresh();
            clearForm();
        } catch (IllegalArgumentException ex) {
            UiUtils.error(this, ex.getMessage());
        }
    }

    private void onUpdate() {
        try {
            Student s = fromForm();
            if (!dao.exists(s.getRollNo())) {
                UiUtils.error(this, "No student with roll number '" + s.getRollNo() + "'.");
                return;
            }
            dao.update(s);
            AuditService.log("UPDATE", "student:" + s.getRollNo());
            UiUtils.info(this, "Student updated.");
            refresh();
        } catch (IllegalArgumentException ex) {
            UiUtils.error(this, ex.getMessage());
        }
    }

    private void onDelete() {
        String roll = rollField.getText().trim();
        if (roll.isEmpty()) { UiUtils.error(this, "Select a student to delete."); return; }
        if (!UiUtils.confirm(this, "Delete student " + roll + "? This removes all related registrations/grades.")) return;
        dao.delete(roll);
        AuditService.log("DELETE", "student:" + roll);
        refresh();
        clearForm();
    }
}
