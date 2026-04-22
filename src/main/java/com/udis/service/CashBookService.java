package com.udis.service;

import com.udis.dao.TransactionDao;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CashBookService {

    private final TransactionDao dao = new TransactionDao();

    public static class Summary {
        public final BigDecimal totalIncome;
        public final BigDecimal totalExpenditure;
        public final BigDecimal balance;
        public Summary(BigDecimal i, BigDecimal e, BigDecimal b) {
            this.totalIncome = i; this.totalExpenditure = e; this.balance = b;
        }
    }

    public Summary summary(LocalDate from, LocalDate to) {
        BigDecimal inc = dao.sum("INCOME", from, to);
        BigDecimal exp = dao.sum("EXPENDITURE", from, to);
        return new Summary(inc, exp, inc.subtract(exp));
    }

    public Summary overall() {
        BigDecimal inc = dao.totalIncome();
        BigDecimal exp = dao.totalExpenditure();
        return new Summary(inc, exp, inc.subtract(exp));
    }
}
