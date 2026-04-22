package com.udis.service;

import com.udis.db.Database;
import com.udis.model.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class AuditService {

    private AuditService() { }

    public static void log(String action, String entity) {
        User u = AuthService.currentUser();
        log(u == null ? "anonymous" : u.getUsername(), action, entity);
    }

    public static void log(String username, String action, String entity) {
        String sql = "INSERT INTO audit_log (username, action, entity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, action);
            ps.setString(3, entity);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Audit log failed: " + e.getMessage());
        }
    }
}
