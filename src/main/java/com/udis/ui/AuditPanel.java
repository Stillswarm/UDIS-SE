package com.udis.ui;

import com.udis.dao.AuditDao;
import com.udis.model.AuditEntry;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class AuditPanel extends JPanel {

    private final AuditDao dao = new AuditDao();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "When", "Username", "Action", "Entity"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
        @Override public Class<?> getColumnClass(int c) { return c == 0 ? Integer.class : String.class; }
    };
    private final JTable table = new JTable(model);

    public AuditPanel() {
        setLayout(new BorderLayout(8, 8));
        UiUtils.padded(this);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refresh());
        top.add(refresh);
        add(top, BorderLayout.NORTH);

        refresh();
    }

    private void refresh() {
        model.setRowCount(0);
        for (AuditEntry e : dao.findRecent(500)) {
            model.addRow(new Object[]{e.getId(), e.getAt(), e.getUsername(), e.getAction(), e.getEntity()});
        }
    }
}
