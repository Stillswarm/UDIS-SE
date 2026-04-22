package com.udis.dao;

import com.udis.db.Database;
import com.udis.model.InventoryItem;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InventoryDao {

    public List<InventoryItem> findAll() {
        List<InventoryItem> list = new ArrayList<>();
        String sql = "SELECT item_id, name, category, serial_number, location, acquisition_date, condition_status " +
                     "FROM inventory_item ORDER BY item_id";
        try (Statement st = Database.get().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void insert(InventoryItem i) {
        String sql = "INSERT INTO inventory_item (name, category, serial_number, location, acquisition_date, condition_status) " +
                     "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, i.getName());
            ps.setString(2, i.getCategory());
            ps.setString(3, i.getSerialNumber());
            ps.setString(4, i.getLocation());
            ps.setDate(5, i.getAcquisitionDate() == null ? null : Date.valueOf(i.getAcquisitionDate()));
            ps.setString(6, i.getConditionStatus());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("insert inventory failed: " + e.getMessage(), e); }
    }

    public void update(InventoryItem i) {
        String sql = "UPDATE inventory_item SET name=?, category=?, serial_number=?, location=?, acquisition_date=?, condition_status=? " +
                     "WHERE item_id=?";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, i.getName());
            ps.setString(2, i.getCategory());
            ps.setString(3, i.getSerialNumber());
            ps.setString(4, i.getLocation());
            ps.setDate(5, i.getAcquisitionDate() == null ? null : Date.valueOf(i.getAcquisitionDate()));
            ps.setString(6, i.getConditionStatus());
            ps.setInt(7, i.getItemId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void updateStatus(int itemId, String status) {
        try (PreparedStatement ps = Database.get().prepareStatement(
                "UPDATE inventory_item SET condition_status=? WHERE item_id=?")) {
            ps.setString(1, status);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(int itemId) {
        try (PreparedStatement ps = Database.get().prepareStatement("DELETE FROM inventory_item WHERE item_id=?")) {
            ps.setInt(1, itemId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private InventoryItem map(ResultSet rs) throws SQLException {
        InventoryItem i = new InventoryItem();
        i.setItemId(rs.getInt("item_id"));
        i.setName(rs.getString("name"));
        i.setCategory(rs.getString("category"));
        i.setSerialNumber(rs.getString("serial_number"));
        i.setLocation(rs.getString("location"));
        Date d = rs.getDate("acquisition_date");
        i.setAcquisitionDate(d == null ? null : d.toLocalDate());
        i.setConditionStatus(rs.getString("condition_status"));
        return i;
    }
}
