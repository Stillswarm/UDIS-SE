package com.udis;

import com.formdev.flatlaf.FlatLightLaf;
import com.udis.db.Database;
import com.udis.ui.LoginFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) { }

        try {
            Database.init();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to initialize database:\n" + e.getMessage(),
                    "UDIS - Startup Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
