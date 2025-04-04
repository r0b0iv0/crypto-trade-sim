package com.cryptotrade.sim.demo.repos;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cryptotrade.sim.demo.models.User;

@Repository
public class UsersRepository {
    private final JdbcTemplate jdbcTemplate;
    private TransactionsRepository transactionsRepository;

    public UsersRepository(JdbcTemplate jdbcTemplate, TransactionsRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> getAllUsersList() {
        String sql = "SELECT id, name, balance FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    public User getSingleUser(int id) {
        String sql = "SELECT id, name, balance FROM users WHERE users.id = ?";
        return jdbcTemplate.queryForObject(sql, new UserRowMapper(), id);

    }

    public void userBuysCrypto(int userId, String cryptoSymbol, BigDecimal cryptoAmount, BigDecimal cost) {

        BigDecimal userBalance = getUserBalance(userId);

        if (userBalance == null) {
            throw new RuntimeException("User not found.");
        }
        if (userBalance.compareTo(cost) < 0) {
            throw new RuntimeException("Insufficient balance.");
        }

        String cryptoIdQuery = "SELECT id FROM cryptocurrencies WHERE symbol = ?";
        Integer cryptoId = jdbcTemplate.queryForObject(cryptoIdQuery, Integer.class, cryptoSymbol);

        if (cryptoId == null) {
            throw new RuntimeException("Cryptocurrency not found.");
        }
        BigDecimal currentCryptoAmount = null;
        try {

            String holdingsQuery = "SELECT amount FROM user_crypto_holdings WHERE user_id = ? AND crypto_id = ?";
            currentCryptoAmount = jdbcTemplate.queryForObject(holdingsQuery, BigDecimal.class, userId, cryptoId);
        } catch (Exception e) {
            System.out.println("User don't have this crypto " + e);
        }

        if (currentCryptoAmount == null) {

            String insertHoldingQuery = "INSERT INTO user_crypto_holdings (user_id, crypto_id, amount) VALUES (?, ?, ?)";
            jdbcTemplate.update(insertHoldingQuery, userId, cryptoId, cryptoAmount);
        } else {

            String updateHoldingQuery = "UPDATE user_crypto_holdings SET amount = amount + ? WHERE user_id = ? AND crypto_id = ?";
            jdbcTemplate.update(updateHoldingQuery, cryptoAmount, userId, cryptoId);
        }

        updateUserBalance(userId, cost);

        transactionsRepository.recordTransaction(userId, "BUY", cryptoSymbol, cryptoAmount, cost);

        System.out.println("Succesfull");
    }

    public String sellCrypto(int userId, String cryptoSymbol, BigDecimal cryptoAmount, BigDecimal usdPrice) {

        BigDecimal totalValue = cryptoAmount.multiply(usdPrice);

        BigDecimal currentHolding = getCryptoHolding(userId, cryptoSymbol);
        if (currentHolding.compareTo(cryptoAmount) < 0) {
            throw new RuntimeException("Insufficient " + cryptoSymbol + " balance");
        }

        updateCryptoHolding(userId, cryptoSymbol, cryptoAmount.negate());

        updateUserBalance(userId, totalValue);

        transactionsRepository.recordTransaction(userId, "SELL", cryptoSymbol, cryptoAmount, usdPrice);

        return String.format(
                "Sold %s %s for $%.2f. New balance: $%.2f",
                cryptoAmount, cryptoSymbol, totalValue,
                getUserBalance(userId));
    }

    private BigDecimal getCryptoHolding(int userId, String symbol) {
        String sql = "SELECT uch.amount FROM user_crypto_holdings uch " +
                "JOIN cryptocurrencies c ON uch.crypto_id = c.id " +
                "WHERE uch.user_id = ? AND c.symbol = ?";

        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId, symbol);
        } catch (EmptyResultDataAccessException e) {
            return BigDecimal.ZERO;
        }
    }

    private void updateCryptoHolding(int userId, String symbol, BigDecimal delta) {

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

    private BigDecimal getUserBalance(int userId) {
        return jdbcTemplate.queryForObject(
                "SELECT balance FROM users WHERE id = ?",
                BigDecimal.class, userId);
    }

    private void updateUserBalance(int userId, BigDecimal totalValue) {
        jdbcTemplate.update(
                "UPDATE users SET balance = balance + ? WHERE id = ?",
                totalValue, userId);
    }

    public void resetUserData(int userId) {
        String deleteTransactions = "DELETE FROM transactions WHERE user_id = ?";
        String deleteHoldings = "DELETE FROM user_crypto_holdings WHERE user_id = ?";
        String resetBalance = "UPDATE users SET balance = 10000.00 WHERE id = ?";

        jdbcTemplate.update(deleteTransactions, userId);
        jdbcTemplate.update(deleteHoldings, userId);
        jdbcTemplate.update(resetBalance, userId);
    }

}

class UserRowMapper implements RowMapper<User> {

    public User mapRow(ResultSet rs, int row) throws SQLException {
        return new User(rs.getInt("id"), rs.getString("name"), rs.getBigDecimal("balance"));
    }
}
