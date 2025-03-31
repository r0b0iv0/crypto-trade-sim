
DROP DATABASE IF EXISTS crypto_trading;
CREATE DATABASE crypto_trading;
USE crypto_trading;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    balance DECIMAL(15,2) DEFAULT 10000.00
);


CREATE TABLE transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    transaction_type ENUM('BUY', 'SELL') NOT NULL,
    crypto_name VARCHAR(50) NOT NULL,
    crypto_amount DECIMAL(18,8) NOT NULL,
    currency ENUM('USD', 'BGN') NOT NULL,
    currency_amount DECIMAL(18,8) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE cryptocurrencies (
    id INT PRIMARY KEY AUTO_INCREMENT,
    symbol VARCHAR(10) UNIQUE NOT NULL  
);

CREATE TABLE user_crypto_holdings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    crypto_id INT NOT NULL,
    amount DECIMAL(18,8) NOT NULL CHECK (amount >= 0),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (crypto_id) REFERENCES cryptocurrencies(id) ON DELETE CASCADE,
    UNIQUE (user_id, crypto_id)
);


