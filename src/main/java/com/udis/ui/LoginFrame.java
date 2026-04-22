package com.udis.ui;

import com.udis.db.Database;
import com.udis.model.User;
import com.udis.service.AuditService;
import com.udis.service.AuthService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        super("UDIS - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("University Department Information System", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        panel.add(title, c);

        JLabel subtitle = new JLabel("Please sign in to continue", SwingConstants.CENTER);
        c.gridy = 1; panel.add(subtitle, c);

        c.gridwidth = 1;
        c.gridy = 2; c.gridx = 0; panel.add(new JLabel("Username"), c);
        JTextField userField = new JTextField(16);
        c.gridx = 1; panel.add(userField, c);

        c.gridy = 3; c.gridx = 0; panel.add(new JLabel("Password"), c);
        JPasswordField pwField = new JPasswordField(16);
        c.gridx = 1; panel.add(pwField, c);

        JButton loginBtn = new JButton("Sign In");
        c.gridy = 4; c.gridx = 0; c.gridwidth = 2; panel.add(loginBtn, c);

        JLabel hint = new JLabel("<html><div style='text-align:center;color:#777;font-size:10px;'>"
                + "Demo: secretary / secretary123 &nbsp;|&nbsp; hod / hod123<br>"
                + "faculty / faculty123 &nbsp;|&nbsp; admin / admin123</div></html>",
                SwingConstants.CENTER);
        c.gridy = 5; panel.add(hint, c);

        loginBtn.addActionListener(e -> attemptLogin(userField.getText(), new String(pwField.getPassword())));
        getRootPane().setDefaultButton(loginBtn);

        setContentPane(panel);
        pack();
        setMinimumSize(new Dimension(420, getHeight()));
        setLocationRelativeTo(null);
    }

    private void attemptLogin(String username, String password) {
        if (username.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Enter both username and password.",
                    "Missing Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User user = new AuthService().login(username.trim(), password);
        if (user == null) {
            AuditService.log(username.trim(), "LOGIN_FAILED", "app_user");
            JOptionPane.showMessageDialog(this, "Invalid credentials.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (Database.maintenanceMode() && !user.isAdmin()) {
            AuditService.log(user.getUsername(), "LOGIN_DENIED_MAINTENANCE", "app_user");
            AuthService.logout();
            JOptionPane.showMessageDialog(this,
                    "System is under maintenance. Only administrators may log in.",
                    "Maintenance Mode", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AuditService.log(user.getUsername(), "LOGIN", "app_user");
        dispose();
        new MainFrame(user).setVisible(true);
    }
}
