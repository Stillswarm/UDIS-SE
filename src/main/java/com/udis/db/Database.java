package com.udis.db;

import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public final class Database {

    private static Connection connection;
    private static final Properties CONFIG = new Properties();

    private Database() { }

    public static synchronized void init() {
        if (connection != null) return;
        try (InputStream in = Database.class.getResourceAsStream("/config.properties")) {
            if (in == null) throw new IllegalStateException("config.properties not found on classpath");
            CONFIG.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }

        try {
            connection = DriverManager.getConnection(
                    CONFIG.getProperty("jdbc.url"),
                    CONFIG.getProperty("jdbc.user"),
                    CONFIG.getProperty("jdbc.password"));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to MySQL. Check config.properties and that MySQL is running.", e);
        }

        runSqlScript("/schema.sql");
        if (isFreshInstall()) {
            runSqlScript("/seed.sql");
            seedUsers();
        }
    }

    public static Connection get() {
        if (connection == null) init();
        return connection;
    }

    public static String property(String key) {
        return CONFIG.getProperty(key);
    }

    public static boolean maintenanceMode() {
        return Boolean.parseBoolean(CONFIG.getProperty("maintenance", "false"));
    }

    private static boolean isFreshInstall() {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM app_user")) {
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check fresh install state", e);
        }
    }

    private static void runSqlScript(String classpathResource) {
        StringBuilder sql = new StringBuilder();
        try (InputStream in = Database.class.getResourceAsStream(classpathResource)) {
            if (in == null) throw new IllegalStateException("Resource not found: " + classpathResource);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("--")) continue;
                    sql.append(line).append('\n');
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read " + classpathResource, e);
        }

        try (Statement st = connection.createStatement()) {
            for (String stmt : sql.toString().split(";")) {
                String s = stmt.trim();
                if (!s.isEmpty()) st.execute(s);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute " + classpathResource + ": " + e.getMessage(), e);
        }
    }

    private static void seedUsers() {
        String sql = "INSERT INTO app_user (username, password_hash, role, full_name) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            addUser(ps, "secretary", "secretary123", "SECRETARY", "Dept. Secretary");
            addUser(ps, "hod",       "hod123",       "HOD",       "Head of Department");
            addUser(ps, "faculty",   "faculty123",   "FACULTY",   "Dr. J. Pati");
            addUser(ps, "admin",     "admin123",     "ADMIN",     "System Administrator");
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to seed users", e);
        }
    }

    private static void addUser(PreparedStatement ps, String u, String pw, String role, String name) throws SQLException {
        ps.setString(1, u);
        ps.setString(2, BCrypt.hashpw(pw, BCrypt.gensalt()));
        ps.setString(3, role);
        ps.setString(4, name);
        ps.addBatch();
    }
}
