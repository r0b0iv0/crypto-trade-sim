package com.cryptotrade.sim.demo.models;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SellCryptoRequest {
    private int userId;
    private String cryptoSymbol;
    private BigDecimal amount;
    private BigDecimal usdPrice;
    
}