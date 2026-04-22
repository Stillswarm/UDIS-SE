package com.udis.dao;

import com.udis.db.Database;
import com.udis.model.Transaction;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {

    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT txn_id, txn_date, description, category, amount FROM txn ORDER BY txn_date DESC, txn_id DESC";
        try (Statement st = Database.get().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Transaction> findBetween(LocalDate from, LocalDate to) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT txn_id, txn_date, description, category, amount FROM txn " +
                     "WHERE txn_date BETWEEN ? AND ? ORDER BY txn_date, txn_id";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void insert(Transaction t) {
        String sql = "INSERT INTO txn (txn_date, description, category, amount) VALUES (?,?,?,?)";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(t.getDate()));
            ps.setString(2, t.getDescription());
            ps.setString(3, t.getCategory());
            ps.setBigDecimal(4, t.getAmount());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("insert txn failed: " + e.getMessage(), e); }
    }

    public void delete(int id) {
        try (PreparedStatement ps = Database.get().prepareStatement("DELETE FROM txn WHERE txn_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public BigDecimal totalIncome() { return sum("INCOME", null, null); }
    public BigDecimal totalExpenditure() { return sum("EXPENDITURE", null, null); }

    public BigDecimal sum(String category, LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder("SELECT COALESCE(SUM(amount),0) FROM txn WHERE category=?");
        if (from != null && to != null) sql.append(" AND txn_date BETWEEN ? AND ?");
        try (PreparedStatement ps = Database.get().prepareStatement(sql.toString())) {
            ps.setString(1, category);
            if (from != null && to != null) {
                ps.setDate(2, Date.valueOf(from));
                ps.setDate(3, Date.valueOf(to));
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public BigDecimal balance() {
        return totalIncome().subtract(totalExpenditure());
    }

    private Transaction map(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTxnId(rs.getInt("txn_id"));
        t.setDate(rs.getDate("txn_date").toLocalDate());
        t.setDescription(rs.getString("description"));
        t.setCategory(rs.getString("category"));
        t.setAmount(rs.getBigDecimal("amount"));
        return t;
    }
}
