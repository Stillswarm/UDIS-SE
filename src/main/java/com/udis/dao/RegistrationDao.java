package com.udis.dao;

import com.udis.db.Database;
import com.udis.model.Registration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDao {

    public List<Registration> findByStudent(String rollNo) {
        String sql = "SELECT reg_id, roll_no, course_id, semester, year, status " +
                     "FROM registration WHERE roll_no=? ORDER BY year, semester, course_id";
        return query(sql, ps -> ps.setString(1, rollNo));
    }

    public List<Registration> findBySemester(String rollNo, int semester, int year) {
        String sql = "SELECT reg_id, roll_no, course_id, semester, year, status " +
                     "FROM registration WHERE roll_no=? AND semester=? AND year=? ORDER BY course_id";
        return query(sql, ps -> {
            ps.setString(1, rollNo);
            ps.setInt(2, semester);
            ps.setInt(3, year);
        });
    }

    public boolean hasCompleted(String rollNo, String courseId) {
        String sql = "SELECT 1 FROM registration WHERE roll_no=? AND course_id=? AND status='COMPLETED' LIMIT 1";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, rollNo);
            ps.setString(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean alreadyRegistered(String rollNo, String courseId, int semester, int year) {
        String sql = "SELECT 1 FROM registration WHERE roll_no=? AND course_id=? AND semester=? AND year=? LIMIT 1";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, rollNo);
            ps.setString(2, courseId);
            ps.setInt(3, semester);
            ps.setInt(4, year);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void insert(Registration r) {
        String sql = "INSERT INTO registration (roll_no, course_id, semester, year, status) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, r.getRollNo());
            ps.setString(2, r.getCourseId());
            ps.setInt(3, r.getSemester());
            ps.setInt(4, r.getYear());
            ps.setString(5, r.getStatus() == null ? "REGISTERED" : r.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("insert registration failed: " + e.getMessage(), e); }
    }

    public void updateStatus(int regId, String status) {
        try (PreparedStatement ps = Database.get().prepareStatement(
                "UPDATE registration SET status=? WHERE reg_id=?")) {
            ps.setString(1, status);
            ps.setInt(2, regId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(int regId) {
        try (PreparedStatement ps = Database.get().prepareStatement("DELETE FROM registration WHERE reg_id=?")) {
            ps.setInt(1, regId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private interface Binder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Registration> query(String sql, Binder binder) {
        List<Registration> list = new ArrayList<>();
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Registration(
                            rs.getInt("reg_id"), rs.getString("roll_no"),
                            rs.getString("course_id"), rs.getInt("semester"),
                            rs.getInt("year"), rs.getString("status")));
                }
            }
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
