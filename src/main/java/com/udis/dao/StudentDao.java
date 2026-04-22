package com.udis.dao;

import com.udis.db.Database;
import com.udis.model.Student;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {

    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT roll_no, name, dob, gender, address, contact, program, batch " +
                     "FROM student ORDER BY roll_no";
        try (Statement st = Database.get().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("findAll students failed", e);
        }
    }

    public Student findByRollNo(String rollNo) {
        String sql = "SELECT roll_no, name, dob, gender, address, contact, program, batch " +
                     "FROM student WHERE roll_no = ?";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByRollNo failed", e);
        }
    }

    public boolean exists(String rollNo) {
        return findByRollNo(rollNo) != null;
    }

    public void insert(Student s) {
        String sql = "INSERT INTO student (roll_no, name, dob, gender, address, contact, program, batch) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            bind(ps, s);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("insert student failed: " + e.getMessage(), e);
        }
    }

    public void update(Student s) {
        String sql = "UPDATE student SET name=?, dob=?, gender=?, address=?, contact=?, program=?, batch=? " +
                     "WHERE roll_no=?";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setDate(2, s.getDob() == null ? null : Date.valueOf(s.getDob()));
            ps.setString(3, s.getGender());
            ps.setString(4, s.getAddress());
            ps.setString(5, s.getContact());
            ps.setString(6, s.getProgram());
            ps.setString(7, s.getBatch());
            ps.setString(8, s.getRollNo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("update student failed", e);
        }
    }

    public void delete(String rollNo) {
        try (PreparedStatement ps = Database.get().prepareStatement("DELETE FROM student WHERE roll_no=?")) {
            ps.setString(1, rollNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("delete student failed", e);
        }
    }

    private void bind(PreparedStatement ps, Student s) throws SQLException {
        ps.setString(1, s.getRollNo());
        ps.setString(2, s.getName());
        ps.setDate(3, s.getDob() == null ? null : Date.valueOf(s.getDob()));
        ps.setString(4, s.getGender());
        ps.setString(5, s.getAddress());
        ps.setString(6, s.getContact());
        ps.setString(7, s.getProgram());
        ps.setString(8, s.getBatch());
    }

    private Student map(ResultSet rs) throws SQLException {
        Date dob = rs.getDate("dob");
        return new Student(
                rs.getString("roll_no"),
                rs.getString("name"),
                dob == null ? null : dob.toLocalDate(),
                rs.getString("gender"),
                rs.getString("address"),
                rs.getString("contact"),
                rs.getString("program"),
                rs.getString("batch"));
    }
}
