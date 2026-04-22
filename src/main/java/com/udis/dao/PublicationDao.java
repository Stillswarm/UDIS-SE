package com.udis.dao;

import com.udis.db.Database;
import com.udis.model.Publication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PublicationDao {

    public List<Publication> findAll() {
        List<Publication> list = new ArrayList<>();
        String sql = "SELECT pub_id, title, authors, journal, year, doi FROM publication ORDER BY year DESC, pub_id DESC";
        try (Statement st = Database.get().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void insert(Publication p) {
        String sql = "INSERT INTO publication (title, authors, journal, year, doi) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            bind(ps, p);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("insert publication failed: " + e.getMessage(), e); }
    }

    public void update(Publication p) {
        String sql = "UPDATE publication SET title=?, authors=?, journal=?, year=?, doi=? WHERE pub_id=?";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            bind(ps, p);
            ps.setInt(6, p.getPubId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(int id) {
        try (PreparedStatement ps = Database.get().prepareStatement("DELETE FROM publication WHERE pub_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private void bind(PreparedStatement ps, Publication p) throws SQLException {
        ps.setString(1, p.getTitle());
        ps.setString(2, p.getAuthors());
        ps.setString(3, p.getJournal());
        if (p.getYear() == null) ps.setNull(4, java.sql.Types.INTEGER); else ps.setInt(4, p.getYear());
        ps.setString(5, p.getDoi());
    }

    private Publication map(ResultSet rs) throws SQLException {
        Publication p = new Publication();
        p.setPubId(rs.getInt("pub_id"));
        p.setTitle(rs.getString("title"));
        p.setAuthors(rs.getString("authors"));
        p.setJournal(rs.getString("journal"));
        int y = rs.getInt("year");
        p.setYear(rs.wasNull() ? null : y);
        p.setDoi(rs.getString("doi"));
        return p;
    }
}
