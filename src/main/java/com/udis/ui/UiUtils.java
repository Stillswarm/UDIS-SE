package com.udis.ui;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class UiUtils {

    private UiUtils() { }

    public static GridBagConstraints gbc(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x; c.gridy = y;
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        return c;
    }

    public static void padded(JComponent c) {
        c.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static LocalDate parseDate(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        try {
            return LocalDate.parse(t);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date '" + s + "' (expected YYYY-MM-DD)");
        }
    }

    public static Integer parseIntOrNull(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid integer: " + s); }
    }

    public static int parseInt(String s, String field) {
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(field + " must be an integer"); }
    }
}
