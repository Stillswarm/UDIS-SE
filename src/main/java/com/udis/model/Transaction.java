package com.udis.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private int txnId;
    private LocalDate date;
    private String description;
    private String category; // INCOME or EXPENDITURE
    private BigDecimal amount;

    public Transaction() { }

    public int getTxnId() { return txnId; }
    public void setTxnId(int txnId) { this.txnId = txnId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
