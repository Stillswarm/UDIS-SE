package com.udis.ui;

import com.udis.model.User;
import com.udis.service.AuthService;
import com.udis.service.AuditService;
import com.udis.db.Database;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;

public class MainFrame extends JFrame {

    public MainFrame(User user) {
        super("UDIS - " + user.getFullName() + " (" + user.getRole() + ")");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        setJMenuBar(buildMenuBar());

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        JLabel title = new JLabel("University Department Information System");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        JLabel who = new JLabel("Signed in: " + user.getFullName() + "  |  Role: " + user.getRole(),
                SwingConstants.RIGHT);
        header.add(title, BorderLayout.WEST);
        header.add(who, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        if (AuthService.canAccess(AuthService.Module.STUDENT))      tabs.addTab("Students",      new StudentPanel());
        if (AuthService.canAccess(AuthService.Module.COURSE))       tabs.addTab("Courses",       new CoursePanel());
        if (AuthService.canAccess(AuthService.Module.REGISTRATION)) tabs.addTab("Registration",  new RegistrationPanel());
        if (AuthService.canAccess(AuthService.Module.GRADE))        tabs.addTab("Grades",        new GradePanel());
        if (AuthService.canAccess(AuthService.Module.INVENTORY))    tabs.addTab("Inventory",     new InventoryPanel());
        if (AuthService.canAccess(AuthService.Module.FINANCE))      tabs.addTab("Finance",       new FinancePanel());
        if (AuthService.canAccess(AuthService.Module.RESEARCH))     tabs.addTab("Research",      new ResearchPanel());
        if (AuthService.canAccess(AuthService.Module.QUERY))        tabs.addTab("Student Query", new QueryPanel());
        if (AuthService.canAccess(AuthService.Module.AUDIT))        tabs.addTab("Audit Log",     new AuditPanel());

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem backup = new JMenuItem("Backup Database...");
        backup.addActionListener(e -> backupDatabase());
        JMenuItem logout = new JMenuItem("Logout");
        logout.addActionListener(e -> doLogout());
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        file.add(backup);
        file.addSeparator();
        file.add(logout);
        file.add(exit);

        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem("About UDIS");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "University Department Information System\nVersion 1.0\nIIIT Ranchi - Software Engineering Project",
                "About", JOptionPane.INFORMATION_MESSAGE));
        help.add(about);

        mb.add(file);
        mb.add(help);
        return mb;
    }

    private void doLogout() {
        AuditService.log("LOGOUT", "app_user");
        AuthService.logout();
        dispose();
        new LoginFrame().setVisible(true);
    }

    private void backupDatabase() {
        String url = Database.property("jdbc.url");
        String user = Database.property("jdbc.user");
        String pw = Database.property("jdbc.password");
        String dbName = extractDbName(url);
        File out = new File(System.getProperty("user.home"),
                "udis_backup_" + System.currentTimeMillis() + ".sql");
        try {
            ProcessBuilder pb = new ProcessBuilder("mysqldump", "-u" + user, "-p" + pw, dbName);
            pb.redirectOutput(out);
            pb.redirectErrorStream(false);
            Process p = pb.start();
            int code = p.waitFor();
            if (code == 0) {
                AuditService.log("BACKUP", "database");
                JOptionPane.showMessageDialog(this, "Backup saved to:\n" + out.getAbsolutePath(),
                        "Backup Complete", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "mysqldump exited with code " + code + ". Ensure mysqldump is on PATH.",
                        "Backup Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Backup failed: " + ex.getMessage() + "\nEnsure mysqldump is installed and on PATH.",
                    "Backup Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String extractDbName(String url) {
        int slash = url.lastIndexOf('/');
        int q = url.indexOf('?', slash);
        return url.substring(slash + 1, q < 0 ? url.length() : q);
    }
}
