package com.udis.dao;

import com.udis.db.Database;
import com.udis.model.Course;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {

    public List<Course> findAll() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT course_id, course_name, credits, semester, prerequisite_id " +
                     "FROM course ORDER BY semester, course_id";
        try (Statement st = Database.get().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("findAll courses failed", e);
        }
    }

    public Course findById(String id) {
        String sql = "SELECT course_id, course_name, credits, semester, prerequisite_id FROM course WHERE course_id=?";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById course failed", e);
        }
    }

    public boolean exists(String id) { return findById(id) != null; }

    public void insert(Course c) {
        String sql = "INSERT INTO course (course_id, course_name, credits, semester, prerequisite_id) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, c.getCourseId());
            ps.setString(2, c.getCourseName());
            ps.setInt(3, c.getCredits());
            ps.setInt(4, c.getSemester());
            if (c.getPrerequisiteId() == null || c.getPrerequisiteId().isBlank()) {
                ps.setNull(5, java.sql.Types.VARCHAR);
            } else {
                ps.setString(5, c.getPrerequisiteId());
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("insert course failed: " + e.getMessage(), e);
        }
    }

    public void update(Course c) {
        String sql = "UPDATE course SET course_name=?, credits=?, semester=?, prerequisite_id=? WHERE course_id=?";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, c.getCourseName());
            ps.setInt(2, c.getCredits());
            ps.setInt(3, c.getSemester());
            if (c.getPrerequisiteId() == null || c.getPrerequisiteId().isBlank()) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, c.getPrerequisiteId());
            }
            ps.setString(5, c.getCourseId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("update course failed", e);
        }
    }

    public void delete(String id) {
        try (PreparedStatement ps = Database.get().prepareStatement("DELETE FROM course WHERE course_id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("delete course failed: " + e.getMessage(), e);
        }
    }

    private Course map(ResultSet rs) throws SQLException {
        return new Course(
                rs.getString("course_id"),
                rs.getString("course_name"),
                rs.getInt("credits"),
                rs.getInt("semester"),
                rs.getString("prerequisite_id"));
    }
}
