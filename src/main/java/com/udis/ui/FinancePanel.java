package com.udis.ui;

import com.udis.dao.TransactionDao;
import com.udis.model.Transaction;
import com.udis.service.AuditService;
import com.udis.service.AuthService;
import com.udis.service.CashBookService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FinancePanel extends JPanel {

    private final TransactionDao dao = new TransactionDao();
    private final CashBookService service = new CashBookService();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Date", "Description", "Category", "Amount"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
        @Override public Class<?> getColumnClass(int c) {
            if (c == 0) return Integer.class;
            if (c == 4) return BigDecimal.class;
            return String.class;
        }
    };
    private final JTable table = new JTable(model);

    private final JLabel balanceLabel = new JLabel(" ");

    private final JTextField dateField = new JTextField(10);
    private final JTextField descField = new JTextField(22);
    private final JComboBox<String> categoryBox = new JComboBox<>(new String[]{"INCOME", "EXPENDITURE"});
    private final JTextField amountField = new JTextField(10);

    private final JButton addBtn = new JButton("Add Transaction");
    private final JButton deleteBtn = new JButton("Delete");
    private final JButton summaryBtn = new JButton("Financial Summary...");
    private final JButton clearBtn = new JButton("Clear");

    public FinancePanel() {
        setLayout(new BorderLayout(8, 8));
        UiUtils.padded(this);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        balanceLabel.setFont(balanceLabel.getFont().deriveFont(Font.BOLD, 16f));
        balanceLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(balanceLabel, BorderLayout.NORTH);

        add(buildForm(), BorderLayout.SOUTH);

        boolean canWrite = AuthService.canWrite(AuthService.Module.FINANCE);
        addBtn.setEnabled(canWrite);
        deleteBtn.setEnabled(canWrite);

        addBtn.addActionListener(e -> onAdd());
        deleteBtn.addActionListener(e -> onDelete());
        summaryBtn.addActionListener(e -> openSummary());
        clearBtn.addActionListener(e -> clearForm());

        refresh();
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("New Transaction"));
        form.add(new JLabel("Date (YYYY-MM-DD)*"), UiUtils.gbc(0, 0)); form.add(dateField,  UiUtils.gbc(1, 0));
        form.add(new JLabel("Category*"),          UiUtils.gbc(2, 0)); form.add(categoryBox,UiUtils.gbc(3, 0));
        form.add(new JLabel("Description"),        UiUtils.gbc(0, 1)); form.add(descField,  UiUtils.gbc(1, 1));
        form.add(new JLabel("Amount*"),            UiUtils.gbc(2, 1)); form.add(amountField,UiUtils.gbc(3, 1));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(addBtn); btns.add(deleteBtn); btns.add(summaryBtn); btns.add(clearBtn);

        JPanel c = new JPanel(new BorderLayout());
        c.add(form, BorderLayout.CENTER);
        c.add(btns, BorderLayout.SOUTH);
        return c;
    }

    private void refresh() {
        model.setRowCount(0);
        List<Transaction> all = dao.findAll();
        for (Transaction t : all) {
            model.addRow(new Object[]{t.getTxnId(), t.getDate(), t.getDescription(), t.getCategory(), t.getAmount()});
        }
        CashBookService.Summary s = service.overall();
        balanceLabel.setText(String.format(
                "  Total Income: %s    |    Total Expenditure: %s    |    Balance: %s",
                fmt(s.totalIncome), fmt(s.totalExpenditure), fmt(s.balance)));
    }

    private String fmt(BigDecimal v) {
        return (v == null ? BigDecimal.ZERO : v).setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private void clearForm() {
        dateField.setText(""); descField.setText(""); amountField.setText("");
        categoryBox.setSelectedIndex(0);
        table.clearSelection();
    }

    private void onAdd() {
        try {
            LocalDate d = UiUtils.parseDate(dateField.getText());
            if (d == null) throw new IllegalArgumentException("Date is mandatory.");
            BigDecimal amt;
            try { amt = new BigDecimal(amountField.getText().trim()); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("Amount must be a number."); }
            if (amt.signum() <= 0) throw new IllegalArgumentException("Amount must be positive.");

            Transaction t = new Transaction();
            t.setDate(d);
            t.setDescription(descField.getText().trim());
            t.setCategory((String) categoryBox.getSelectedItem());
            t.setAmount(amt);
            dao.insert(t);
            AuditService.log("INSERT", "transaction:" + t.getCategory() + ":" + t.getAmount());
            refresh(); clearForm();
        } catch (IllegalArgumentException ex) { UiUtils.error(this, ex.getMessage()); }
    }

    private void onDelete() {
        int r = table.getSelectedRow();
        if (r < 0) { UiUtils.error(this, "Select a transaction."); return; }
        int id = (int) model.getValueAt(r, 0);
        if (!UiUtils.confirm(this, "Delete transaction #" + id + "?")) return;
        dao.delete(id);
        AuditService.log("DELETE", "transaction:" + id);
        refresh();
    }

    private void openSummary() {
        JDialog dlg = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Financial Summary", true);
        dlg.setLayout(new BorderLayout(8, 8));
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField from = new JTextField(10);
        JTextField to = new JTextField(10);
        JButton go = new JButton("Compute");
        JLabel result = new JLabel(" ");
        result.setFont(result.getFont().deriveFont(Font.PLAIN, 14f));
        form.add(new JLabel("From (YYYY-MM-DD):")); form.add(from);
        form.add(new JLabel("To:")); form.add(to);
        form.add(go);

        go.addActionListener(ev -> {
            try {
                LocalDate f = UiUtils.parseDate(from.getText());
                LocalDate t = UiUtils.parseDate(to.getText());
                CashBookService.Summary s = (f == null || t == null) ? service.overall() : service.summary(f, t);
                result.setText("<html>Total Income: <b>" + fmt(s.totalIncome) + "</b><br>"
                        + "Total Expenditure: <b>" + fmt(s.totalExpenditure) + "</b><br>"
                        + "Net Balance: <b>" + fmt(s.balance) + "</b></html>");
                AuditService.log("REPORT", "finance_summary");
            } catch (IllegalArgumentException ex) { UiUtils.error(dlg, ex.getMessage()); }
        });

        dlg.add(form, BorderLayout.NORTH);
        result.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        dlg.add(result, BorderLayout.CENTER);
        dlg.setSize(new Dimension(460, 240));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }
}
