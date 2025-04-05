package com.cryptotrade.sim.demo.repos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cryptotrade.sim.demo.enums.TransactionType;
import com.cryptotrade.sim.demo.helpers.Validator;
import com.cryptotrade.sim.demo.models.UserPortfolio;
import com.cryptotrade.sim.demo.services.UserRepositoryService;

@Repository
public class UsersRepository {
    private final JdbcTemplate jdbcTemplate;
    private TransactionsRepository transactionsRepository;
    private UserRepositoryService userRepositoryService;
    private Validator validator;

    public UsersRepository(JdbcTemplate jdbcTemplate, TransactionsRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public String userBuysCrypto(int userId, String cryptoSymbol, BigDecimal cryptoAmount, BigDecimal cost) {

        validator.checkIfBigDecimal(cryptoAmount, "Invalid value of " + cryptoSymbol + " amount ");
        validator.checkIfBigDecimal(cost, "Invalid value of price");

        BigDecimal userBalance = userRepositoryService.getUserBalance(userId);

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

        userRepositoryService.updateCryptoHolding(userId, cryptoSymbol, cryptoAmount);

        userRepositoryService.updateUserBalance(userId, cost, TransactionType.BUY);

        transactionsRepository.recordTransaction(userId, "BUY", cryptoSymbol, cryptoAmount, cost);

        return String.format(
                "Bought %s %s for $%.2f. New balance: $%.2f",
                cryptoAmount, cryptoSymbol, cost,
                userRepositoryService.getUserBalance(userId));
    }

    public String sellCrypto(int userId, String cryptoSymbol, BigDecimal cryptoAmount, BigDecimal usdPrice) {

        validator.checkIfBigDecimal(cryptoAmount, "Invalid value of " + cryptoSymbol + " amount ");
        validator.checkIfBigDecimal(usdPrice, "Invalid value of price");

        BigDecimal totalValue = cryptoAmount.multiply(usdPrice);

        BigDecimal currentHolding = userRepositoryService.getCryptoHolding(userId, cryptoSymbol);
        if (currentHolding.compareTo(cryptoAmount) < 0) {
            throw new RuntimeException("Insufficient " + cryptoSymbol + " balance");
        }

        userRepositoryService.updateCryptoHolding(userId, cryptoSymbol, cryptoAmount.negate());

        userRepositoryService.updateUserBalance(userId, totalValue, TransactionType.SELL);

        transactionsRepository.recordTransaction(userId, "SELL", cryptoSymbol, cryptoAmount, usdPrice);

        return String.format(
                "Sold %s %s for $%.2f. New balance: $%.2f",
                cryptoAmount, cryptoSymbol, totalValue,
                userRepositoryService.getUserBalance(userId));
    }

    public UserPortfolio getUserPortfolio(int userId) {
        String sql = """
                    SELECT u.name, u.balance, c.symbol, h.amount
                    FROM users u
                    LEFT JOIN user_crypto_holdings h ON u.id = h.user_id
                    LEFT JOIN cryptocurrencies c ON h.crypto_id = c.id
                    WHERE u.id = ?
                """;

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userId);

        if (rows.isEmpty()) {
            throw new RuntimeException("User not found: ");
        }

        UserPortfolio portfolio = new UserPortfolio();
        for (Map<String, Object> row : rows) {
            if (portfolio.getName() == null) {
                portfolio.setName((String) row.get("name"));
                portfolio.setBalance((BigDecimal) row.get("balance"));
            }
            String symbol = (String) row.get("symbol");
            BigDecimal amount = (BigDecimal) row.get("amount");
            if (symbol != null && amount != null) {
                portfolio.getHoldings().add(new UserPortfolio.Holding(symbol, amount));
            }
        }

        return portfolio;
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
