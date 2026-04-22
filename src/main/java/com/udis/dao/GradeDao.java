package com.udis.dao;

import com.udis.db.Database;
import com.udis.model.Grade;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GradeDao {

    public List<Grade> findByStudent(String rollNo) {
        String sql = "SELECT g.grade_id, g.roll_no, g.course_id, g.semester, g.year, " +
                     "       g.letter_grade, g.grade_points, c.credits " +
                     "  FROM grade g JOIN course c ON g.course_id = c.course_id " +
                     " WHERE g.roll_no=? ORDER BY g.year, g.semester, g.course_id";
        return runQuery(sql, ps -> ps.setString(1, rollNo));
    }

    public List<Grade> findBySemester(String rollNo, int semester, int year) {
        String sql = "SELECT g.grade_id, g.roll_no, g.course_id, g.semester, g.year, " +
                     "       g.letter_grade, g.grade_points, c.credits " +
                     "  FROM grade g JOIN course c ON g.course_id = c.course_id " +
                     " WHERE g.roll_no=? AND g.semester=? AND g.year=? ORDER BY g.course_id";
        return runQuery(sql, ps -> {
            ps.setString(1, rollNo);
            ps.setInt(2, semester);
            ps.setInt(3, year);
        });
    }

    public Grade find(String rollNo, String courseId, int semester, int year) {
        String sql = "SELECT g.grade_id, g.roll_no, g.course_id, g.semester, g.year, " +
                     "       g.letter_grade, g.grade_points, c.credits " +
                     "  FROM grade g JOIN course c ON g.course_id = c.course_id " +
                     " WHERE g.roll_no=? AND g.course_id=? AND g.semester=? AND g.year=?";
        List<Grade> list = runQuery(sql, ps -> {
            ps.setString(1, rollNo);
            ps.setString(2, courseId);
            ps.setInt(3, semester);
            ps.setInt(4, year);
        });
        return list.isEmpty() ? null : list.get(0);
    }

    public void upsert(Grade g) {
        String sql = "INSERT INTO grade (roll_no, course_id, semester, year, letter_grade, grade_points) " +
                     "VALUES (?,?,?,?,?,?) " +
                     "ON DUPLICATE KEY UPDATE letter_grade=VALUES(letter_grade), grade_points=VALUES(grade_points)";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, g.getRollNo());
            ps.setString(2, g.getCourseId());
            ps.setInt(3, g.getSemester());
            ps.setInt(4, g.getYear());
            ps.setString(5, g.getLetterGrade());
            ps.setDouble(6, g.getGradePoints());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("upsert grade failed: " + e.getMessage(), e); }
    }

    private interface Binder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Grade> runQuery(String sql, Binder binder) {
        List<Grade> list = new ArrayList<>();
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Grade g = new Grade();
                    g.setGradeId(rs.getInt("grade_id"));
                    g.setRollNo(rs.getString("roll_no"));
                    g.setCourseId(rs.getString("course_id"));
                    g.setSemester(rs.getInt("semester"));
                    g.setYear(rs.getInt("year"));
                    g.setLetterGrade(rs.getString("letter_grade"));
                    g.setGradePoints(rs.getDouble("grade_points"));
                    g.setCredits(rs.getInt("credits"));
                    list.add(g);
                }
            }
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
