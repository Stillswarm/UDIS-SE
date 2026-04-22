package com.udis.ui;

import com.udis.dao.PublicationDao;
import com.udis.dao.ResearchDao;
import com.udis.model.Publication;
import com.udis.model.ResearchProject;
import com.udis.service.AuditService;
import com.udis.service.AuthService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.List;

public class ResearchPanel extends JPanel {

    public ResearchPanel() {
        setLayout(new BorderLayout());
        UiUtils.padded(this);
        JTabbedPane sub = new JTabbedPane();
        sub.addTab("Research Projects", new ProjectsTab());
        sub.addTab("Publications", new PublicationsTab());
        add(sub, BorderLayout.CENTER);
    }

    private static class ProjectsTab extends JPanel {
        private final ResearchDao dao = new ResearchDao();
        private final DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Title", "PI", "Funding", "Start", "End", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c == 0 ? Integer.class : String.class; }
        };
        private final JTable table = new JTable(model);

        private final JTextField title = new JTextField(22);
        private final JTextField pi = new JTextField(14);
        private final JTextField funding = new JTextField(12);
        private final JTextField start = new JTextField(10);
        private final JTextField end = new JTextField(10);
        private final JComboBox<String> status = new JComboBox<>(new String[]{"ONGOING", "COMPLETED"});
        private final JButton add = new JButton("Add");
        private final JButton update = new JButton("Update");
        private final JButton delete = new JButton("Delete");
        private final JButton clear = new JButton("Clear");

        ProjectsTab() {
            setLayout(new BorderLayout(8, 8));
            UiUtils.padded(this);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) loadSelection(); });
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(BorderFactory.createTitledBorder("Research Project"));
            form.add(new JLabel("Title*"),   UiUtils.gbc(0, 0)); form.add(title,   UiUtils.gbc(1, 0));
            form.add(new JLabel("PI"),       UiUtils.gbc(2, 0)); form.add(pi,      UiUtils.gbc(3, 0));
            form.add(new JLabel("Funding"),  UiUtils.gbc(0, 1)); form.add(funding, UiUtils.gbc(1, 1));
            form.add(new JLabel("Status"),   UiUtils.gbc(2, 1)); form.add(status,  UiUtils.gbc(3, 1));
            form.add(new JLabel("Start"),    UiUtils.gbc(0, 2)); form.add(start,   UiUtils.gbc(1, 2));
            form.add(new JLabel("End"),      UiUtils.gbc(2, 2)); form.add(end,     UiUtils.gbc(3, 2));

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btns.add(add); btns.add(update); btns.add(delete); btns.add(clear);

            JPanel south = new JPanel(new BorderLayout());
            south.add(form, BorderLayout.CENTER);
            south.add(btns, BorderLayout.SOUTH);
            add(south, BorderLayout.SOUTH);

            boolean canWrite = AuthService.canWrite(AuthService.Module.RESEARCH);
            add.setEnabled(canWrite); update.setEnabled(canWrite); delete.setEnabled(canWrite);

            add.addActionListener(e -> onAdd());
            update.addActionListener(e -> onUpdate());
            delete.addActionListener(e -> onDelete());
            clear.addActionListener(e -> clearForm());
            refresh();
        }

        private void refresh() {
            model.setRowCount(0);
            for (ResearchProject p : dao.findAll()) {
                model.addRow(new Object[]{p.getProjectId(), p.getTitle(), p.getPi(), p.getFundingSource(),
                        p.getStartDate(), p.getEndDate(), p.getStatus()});
            }
        }

        private void loadSelection() {
            int r = table.getSelectedRow();
            if (r < 0) return;
            title.setText(String.valueOf(model.getValueAt(r, 1)));
            pi.setText(String.valueOf(model.getValueAt(r, 2)));
            funding.setText(String.valueOf(model.getValueAt(r, 3)));
            Object s = model.getValueAt(r, 4); start.setText(s == null ? "" : s.toString());
            Object e = model.getValueAt(r, 5); end.setText(e == null ? "" : e.toString());
            status.setSelectedItem(String.valueOf(model.getValueAt(r, 6)));
        }

        private Integer selectedId() {
            int r = table.getSelectedRow();
            return r < 0 ? null : (Integer) model.getValueAt(r, 0);
        }

        private ResearchProject fromForm() {
            if (title.getText().trim().isEmpty()) throw new IllegalArgumentException("Title is mandatory.");
            ResearchProject p = new ResearchProject();
            p.setTitle(title.getText().trim());
            p.setPi(pi.getText().trim());
            p.setFundingSource(funding.getText().trim());
            p.setStartDate(UiUtils.parseDate(start.getText()));
            p.setEndDate(UiUtils.parseDate(end.getText()));
            p.setStatus((String) status.getSelectedItem());
            return p;
        }

        private void clearForm() {
            title.setText(""); pi.setText(""); funding.setText("");
            start.setText(""); end.setText(""); status.setSelectedIndex(0);
            table.clearSelection();
        }

        private void onAdd() {
            try { dao.insert(fromForm()); AuditService.log("INSERT", "research_project"); refresh(); clearForm(); }
            catch (IllegalArgumentException ex) { UiUtils.error(this, ex.getMessage()); }
        }

        private void onUpdate() {
            Integer id = selectedId();
            if (id == null) { UiUtils.error(this, "Select a project."); return; }
            try {
                ResearchProject p = fromForm(); p.setProjectId(id);
                dao.update(p); AuditService.log("UPDATE", "research_project:" + id); refresh();
            } catch (IllegalArgumentException ex) { UiUtils.error(this, ex.getMessage()); }
        }

        private void onDelete() {
            Integer id = selectedId();
            if (id == null) { UiUtils.error(this, "Select a project."); return; }
            if (!UiUtils.confirm(this, "Delete project #" + id + "?")) return;
            dao.delete(id); AuditService.log("DELETE", "research_project:" + id); refresh(); clearForm();
        }
    }

    private static class PublicationsTab extends JPanel {
        private final PublicationDao dao = new PublicationDao();
        private final DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Title", "Authors", "Journal", "Year", "DOI"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c == 0 || c == 4 ? Integer.class : String.class; }
        };
        private final JTable table = new JTable(model);

        private final JTextField title = new JTextField(22);
        private final JTextField authors = new JTextField(18);
        private final JTextField journal = new JTextField(16);
        private final JTextField year = new JTextField(6);
        private final JTextField doi = new JTextField(14);
        private final JButton add = new JButton("Add");
        private final JButton update = new JButton("Update");
        private final JButton delete = new JButton("Delete");
        private final JButton clear = new JButton("Clear");

        PublicationsTab() {
            setLayout(new BorderLayout(8, 8));
            UiUtils.padded(this);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) loadSelection(); });
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(BorderFactory.createTitledBorder("Publication"));
            form.add(new JLabel("Title*"),   UiUtils.gbc(0, 0)); form.add(title,   UiUtils.gbc(1, 0));
            form.add(new JLabel("Authors"),  UiUtils.gbc(2, 0)); form.add(authors, UiUtils.gbc(3, 0));
            form.add(new JLabel("Journal"),  UiUtils.gbc(0, 1)); form.add(journal, UiUtils.gbc(1, 1));
            form.add(new JLabel("Year"),     UiUtils.gbc(2, 1)); form.add(year,    UiUtils.gbc(3, 1));
            form.add(new JLabel("DOI"),      UiUtils.gbc(0, 2)); form.add(doi,     UiUtils.gbc(1, 2));

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btns.add(add); btns.add(update); btns.add(delete); btns.add(clear);

            JPanel south = new JPanel(new BorderLayout());
            south.add(form, BorderLayout.CENTER);
            south.add(btns, BorderLayout.SOUTH);
            add(south, BorderLayout.SOUTH);

            boolean canWrite = AuthService.canWrite(AuthService.Module.RESEARCH);
            add.setEnabled(canWrite); update.setEnabled(canWrite); delete.setEnabled(canWrite);

            add.addActionListener(e -> onAdd());
            update.addActionListener(e -> onUpdate());
            delete.addActionListener(e -> onDelete());
            clear.addActionListener(e -> clearForm());
            refresh();
        }

        private void refresh() {
            model.setRowCount(0);
            List<Publication> pubs = dao.findAll();
            for (Publication p : pubs) {
                model.addRow(new Object[]{p.getPubId(), p.getTitle(), p.getAuthors(), p.getJournal(), p.getYear(), p.getDoi()});
            }
        }

        private void loadSelection() {
            int r = table.getSelectedRow();
            if (r < 0) return;
            title.setText(String.valueOf(model.getValueAt(r, 1)));
            authors.setText(String.valueOf(model.getValueAt(r, 2)));
            journal.setText(String.valueOf(model.getValueAt(r, 3)));
            Object y = model.getValueAt(r, 4); year.setText(y == null ? "" : y.toString());
            doi.setText(String.valueOf(model.getValueAt(r, 5)));
        }

        private Integer selectedId() {
            int r = table.getSelectedRow();
            return r < 0 ? null : (Integer) model.getValueAt(r, 0);
        }

        private Publication fromForm() {
            if (title.getText().trim().isEmpty()) throw new IllegalArgumentException("Title is mandatory.");
            Publication p = new Publication();
            p.setTitle(title.getText().trim());
            p.setAuthors(authors.getText().trim());
            p.setJournal(journal.getText().trim());
            p.setYear(UiUtils.parseIntOrNull(year.getText()));
            p.setDoi(doi.getText().trim());
            return p;
        }

        private void clearForm() {
            title.setText(""); authors.setText(""); journal.setText("");
            year.setText(""); doi.setText("");
            table.clearSelection();
        }

        private void onAdd() {
            try { dao.insert(fromForm()); AuditService.log("INSERT", "publication"); refresh(); clearForm(); }
            catch (IllegalArgumentException ex) { UiUtils.error(this, ex.getMessage()); }
        }

        private void onUpdate() {
            Integer id = selectedId();
            if (id == null) { UiUtils.error(this, "Select a publication."); return; }
            try {
                Publication p = fromForm(); p.setPubId(id);
                dao.update(p); AuditService.log("UPDATE", "publication:" + id); refresh();
            } catch (IllegalArgumentException ex) { UiUtils.error(this, ex.getMessage()); }
        }

        private void onDelete() {
            Integer id = selectedId();
            if (id == null) { UiUtils.error(this, "Select a publication."); return; }
            if (!UiUtils.confirm(this, "Delete publication #" + id + "?")) return;
            dao.delete(id); AuditService.log("DELETE", "publication:" + id); refresh(); clearForm();
        }
    }
}
