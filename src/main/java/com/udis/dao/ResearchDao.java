package com.udis.dao;

import com.udis.db.Database;
import com.udis.model.ResearchProject;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ResearchDao {

    public List<ResearchProject> findAll() {
        List<ResearchProject> list = new ArrayList<>();
        String sql = "SELECT project_id, title, pi, funding_source, start_date, end_date, status " +
                     "FROM research_project ORDER BY project_id DESC";
        try (Statement st = Database.get().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void insert(ResearchProject p) {
        String sql = "INSERT INTO research_project (title, pi, funding_source, start_date, end_date, status) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            bind(ps, p);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("insert research failed: " + e.getMessage(), e); }
    }

    public void update(ResearchProject p) {
        String sql = "UPDATE research_project SET title=?, pi=?, funding_source=?, start_date=?, end_date=?, status=? WHERE project_id=?";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            bind(ps, p);
            ps.setInt(7, p.getProjectId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(int id) {
        try (PreparedStatement ps = Database.get().prepareStatement("DELETE FROM research_project WHERE project_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private void bind(PreparedStatement ps, ResearchProject p) throws SQLException {
        ps.setString(1, p.getTitle());
        ps.setString(2, p.getPi());
        ps.setString(3, p.getFundingSource());
        ps.setDate(4, p.getStartDate() == null ? null : Date.valueOf(p.getStartDate()));
        ps.setDate(5, p.getEndDate() == null ? null : Date.valueOf(p.getEndDate()));
        ps.setString(6, p.getStatus());
    }

    private ResearchProject map(ResultSet rs) throws SQLException {
        ResearchProject p = new ResearchProject();
        p.setProjectId(rs.getInt("project_id"));
        p.setTitle(rs.getString("title"));
        p.setPi(rs.getString("pi"));
        p.setFundingSource(rs.getString("funding_source"));
        Date s = rs.getDate("start_date"); p.setStartDate(s == null ? null : s.toLocalDate());
        Date e = rs.getDate("end_date");   p.setEndDate(e == null ? null : e.toLocalDate());
        p.setStatus(rs.getString("status"));
        return p;
    }
}
