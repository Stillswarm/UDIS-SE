package com.udis.dao;

import com.udis.db.Database;
import com.udis.model.AuditEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AuditDao {

    public List<AuditEntry> findRecent(int limit) {
        List<AuditEntry> list = new ArrayList<>();
        String sql = "SELECT id, username, action, entity, at_time FROM audit_log ORDER BY id DESC LIMIT " + limit;
        try (Statement st = Database.get().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                AuditEntry e = new AuditEntry();
                e.setId(rs.getInt("id"));
                e.setUsername(rs.getString("username"));
                e.setAction(rs.getString("action"));
                e.setEntity(rs.getString("entity"));
                Timestamp t = rs.getTimestamp("at_time");
                e.setAt(t == null ? null : t.toLocalDateTime());
                list.add(e);
            }
            return list;
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }
}
