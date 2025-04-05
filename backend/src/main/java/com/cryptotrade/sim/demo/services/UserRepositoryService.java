
package com.cryptotrade.sim.demo.services;

import java.math.BigDecimal;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cryptotrade.sim.demo.enums.TransactionType;

@Repository
public class UserRepositoryService {

    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryService (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public BigDecimal getCryptoHolding(int userId, String symbol) {
        String sql = "SELECT uch.amount FROM user_crypto_holdings uch " +
                "JOIN cryptocurrencies c ON uch.crypto_id = c.id " +
                "WHERE uch.user_id = ? AND c.symbol = ?";

        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId, symbol);
        } catch (EmptyResultDataAccessException e) {
            return BigDecimal.ZERO;
        }
    }

    public void updateCryptoHolding(int userId, String symbol, BigDecimal delta) {

        Integer cryptoId = jdbcTemplate.queryForObject(
                "SELECT id FROM cryptocurrencies WHERE symbol = ?",
                Integer.class, symbol);

        int updated = jdbcTemplate.update(
                "UPDATE user_crypto_holdings SET amount = amount + ? " +
                        "WHERE user_id = ? AND crypto_id = ?",
                delta, userId, cryptoId);

        if (updated == 0 && delta.compareTo(BigDecimal.ZERO) > 0) {
            jdbcTemplate.update(
                    "INSERT INTO user_crypto_holdings (user_id, crypto_id, amount) " +
                            "VALUES (?, ?, ?)",
                    userId, cryptoId, delta);
        }

        jdbcTemplate.update(
                "DELETE FROM user_crypto_holdings WHERE amount = 0");
    }

    public BigDecimal getUserBalance(int userId) {
        return jdbcTemplate.queryForObject(
                "SELECT balance FROM users WHERE id = ?",
                BigDecimal.class, userId);
    }

    public void updateUserBalance(int userId, BigDecimal totalValue, TransactionType type) {
        String sql;
        if (type.equals(TransactionType.BUY)) {

             sql =   "UPDATE users SET balance = balance - ? WHERE id = ?";

        }
        else if (type.equals(TransactionType.SELL)) {
             sql =   "UPDATE users SET balance = balance + ? WHERE id = ?";
        } else {
            throw new IllegalArgumentException("Unsupported transaction type: " + type);
        }

        int rowsUpdated = jdbcTemplate.update(
            sql,
            totalValue, userId);

            if (rowsUpdated == 0) {
                throw new RuntimeException("Insufficent balance or user not found");
            }
        
       
    }
}