package com.cryptotrade.sim.demo.models;

import java.math.BigDecimal;

public class BuyCryptoRequest {
    private int userId;
    private String cryptoSymbol;
    private BigDecimal cryptoAmount;
    private BigDecimal price;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCryptoSymbol() { return cryptoSymbol; }
    public void setCryptoSymbol(String cryptoSymbol) { this.cryptoSymbol = cryptoSymbol; }

    public BigDecimal getCryptoAmount() { return cryptoAmount; }
    public void setCryptoAmount(BigDecimal cryptoAmount) { this.cryptoAmount = cryptoAmount; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
