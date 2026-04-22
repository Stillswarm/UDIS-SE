package com.udis.dao;

import com.udis.db.Database;
import com.udis.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    public UserRecord findByUsername(String username) {
        String sql = "SELECT user_id, username, password_hash, role, full_name FROM app_user WHERE username = ?";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("role"), rs.getString("full_name"));
                    return new UserRecord(u, rs.getString("password_hash"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load user: " + username, e);
        }
    }

    public static class UserRecord {
        public final User user;
        public final String passwordHash;
        public UserRecord(User user, String passwordHash) {
            this.user = user;
            this.passwordHash = passwordHash;
        }
    }
}
