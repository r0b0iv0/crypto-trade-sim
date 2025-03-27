package com.cryptotrade.sim.demo.repos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cryptotrade.sim.demo.models.Transaction;


@Repository
public class TransactionsRepository {
    private final JdbcTemplate jdbcTemplate;

    public TransactionsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Transaction> getAllUserTransactions(int id) {
        String sql = String.format("SELECT id, transaction_type, crypto_name, crypto_amount," +
         "currency, currency_amount, timestamp FROM users WHERE user_id = %d", id);
        return jdbcTemplate.query(sql, new TransactionRowMapper());
    }
}


class TransactionRowMapper implements RowMapper<Transaction> {
    
    public Transaction mapRow(ResultSet rs, int row) throws SQLException {
        return new Transaction(rs.getInt("id"),
         rs.getString("transaction_type"),
         rs.getString("crypto_name"),
          rs.getBigDecimal("crypto_amount"),
          rs.getString("currency"),
          rs.getBigDecimal("currency_amount"),
          rs.getTimestamp("timestamp"));
    }
}

