package com.udis.ui;

import com.udis.dao.InventoryDao;
import com.udis.model.InventoryItem;
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
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.List;

public class InventoryPanel extends JPanel {

    private final InventoryDao dao = new InventoryDao();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Name", "Category", "Serial No.", "Location", "Acquired", "Condition"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
        @Override public Class<?> getColumnClass(int c) { return c == 0 ? Integer.class : String.class; }
    };
    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

    private final JTextField searchField = new JTextField(18);
    private final JTextField nameField = new JTextField(16);
    private final JTextField categoryField = new JTextField(12);
    private final JTextField serialField = new JTextField(12);
    private final JTextField locationField = new JTextField(16);
    private final JTextField acquiredField = new JTextField(10);
    private final JComboBox<String> conditionBox = new JComboBox<>(new String[]{"GOOD", "FAIR", "POOR", "DISPOSED", "NON_FUNCTIONAL"});

    private final JButton addBtn = new JButton("Add");
    private final JButton updateBtn = new JButton("Update");
    private final JButton disposeBtn = new JButton("Mark Disposed");
    private final JButton deleteBtn = new JButton("Delete");
    private final JButton clearBtn = new JButton("Clear");

    public InventoryPanel() {
        setLayout(new BorderLayout(8, 8));
        UiUtils.padded(this);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search:"));
        top.add(searchField);
        add(top, BorderLayout.NORTH);

        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) loadSelection(); });
        add(new JScrollPane(table), BorderLayout.CENTER);

        add(buildForm(), BorderLayout.SOUTH);

        boolean canWrite = AuthService.canWrite(AuthService.Module.INVENTORY);
        addBtn.setEnabled(canWrite);
        updateBtn.setEnabled(canWrite);
        disposeBtn.setEnabled(canWrite);
        deleteBtn.setEnabled(canWrite);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { applyFilter(); }
            public void removeUpdate(DocumentEvent e)  { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });

        addBtn.addActionListener(e -> onAdd());
        updateBtn.addActionListener(e -> onUpdate());
        disposeBtn.addActionListener(e -> onDispose());
        deleteBtn.addActionListener(e -> onDelete());
        clearBtn.addActionListener(e -> clearForm());

        refresh();
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Inventory Item"));
        form.add(new JLabel("Name*"),      UiUtils.gbc(0, 0)); form.add(nameField,    UiUtils.gbc(1, 0));
        form.add(new JLabel("Category"),   UiUtils.gbc(2, 0)); form.add(categoryField,UiUtils.gbc(3, 0));
        form.add(new JLabel("Serial No."), UiUtils.gbc(0, 1)); form.add(serialField,  UiUtils.gbc(1, 1));
        form.add(new JLabel("Location"),   UiUtils.gbc(2, 1)); form.add(locationField,UiUtils.gbc(3, 1));
        form.add(new JLabel("Acquired (YYYY-MM-DD)"), UiUtils.gbc(0, 2)); form.add(acquiredField, UiUtils.gbc(1, 2));
        form.add(new JLabel("Condition"),  UiUtils.gbc(2, 2)); form.add(conditionBox, UiUtils.gbc(3, 2));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(addBtn); btns.add(updateBtn); btns.add(disposeBtn); btns.add(deleteBtn); btns.add(clearBtn);

        JPanel c = new JPanel(new BorderLayout());
        c.add(form, BorderLayout.CENTER);
        c.add(btns, BorderLayout.SOUTH);
        return c;
    }

    private void applyFilter() {
        String q = searchField.getText().trim();
        sorter.setRowFilter(q.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(q)));
    }

    private void refresh() {
        model.setRowCount(0);
        List<InventoryItem> items = dao.findAll();
        for (InventoryItem i : items) {
            model.addRow(new Object[]{
                    i.getItemId(), i.getName(), i.getCategory(), i.getSerialNumber(),
                    i.getLocation(), i.getAcquisitionDate(), i.getConditionStatus()});
        }
    }

    private Integer selectedId() {
        int vr = table.getSelectedRow();
        if (vr < 0) return null;
        int mr = table.convertRowIndexToModel(vr);
        return (Integer) model.getValueAt(mr, 0);
    }

    private void loadSelection() {
        Integer id = selectedId();
        if (id == null) return;
        int r = table.convertRowIndexToModel(table.getSelectedRow());
        nameField.setText(str(model.getValueAt(r, 1)));
        categoryField.setText(str(model.getValueAt(r, 2)));
        serialField.setText(str(model.getValueAt(r, 3)));
        locationField.setText(str(model.getValueAt(r, 4)));
        Object acq = model.getValueAt(r, 5);
        acquiredField.setText(acq == null ? "" : acq.toString());
        conditionBox.setSelectedItem(str(model.getValueAt(r, 6)));
    }

    private String str(Object o) { return o == null ? "" : o.toString(); }

    private InventoryItem fromForm() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) throw new IllegalArgumentException("Name is mandatory.");
        InventoryItem i = new InventoryItem();
        i.setName(name);
        i.setCategory(categoryField.getText().trim());
        i.setSerialNumber(serialField.getText().trim());
        i.setLocation(locationField.getText().trim());
        i.setAcquisitionDate(UiUtils.parseDate(acquiredField.getText()));
        i.setConditionStatus((String) conditionBox.getSelectedItem());
        return i;
    }

    private void clearForm() {
        nameField.setText(""); categoryField.setText(""); serialField.setText("");
        locationField.setText(""); acquiredField.setText("");
        conditionBox.setSelectedIndex(0);
        table.clearSelection();
    }

    private void onAdd() {
        try {
            InventoryItem i = fromForm();
            dao.insert(i);
            AuditService.log("INSERT", "inventory:" + i.getSerialNumber());
            refresh(); clearForm();
        } catch (IllegalArgumentException ex) { UiUtils.error(this, ex.getMessage()); }
    }

    private void onUpdate() {
        Integer id = selectedId();
        if (id == null) { UiUtils.error(this, "Select an item first."); return; }
        try {
            InventoryItem i = fromForm();
            i.setItemId(id);
            dao.update(i);
            AuditService.log("UPDATE", "inventory:" + id);
            refresh();
        } catch (IllegalArgumentException ex) { UiUtils.error(this, ex.getMessage()); }
    }

    private void onDispose() {
        Integer id = selectedId();
        if (id == null) { UiUtils.error(this, "Select an item first."); return; }
        if (!UiUtils.confirm(this, "Mark item #" + id + " as DISPOSED?")) return;
        dao.updateStatus(id, "DISPOSED");
        AuditService.log("DISPOSE", "inventory:" + id);
        refresh(); clearForm();
    }

    private void onDelete() {
        Integer id = selectedId();
        if (id == null) { UiUtils.error(this, "Select an item first."); return; }
        if (!UiUtils.confirm(this, "Delete item #" + id + "?")) return;
        dao.delete(id);
        AuditService.log("DELETE", "inventory:" + id);
        refresh(); clearForm();
    }
}
