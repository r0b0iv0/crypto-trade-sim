package com.cryptotrade.sim.demo.models;


import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Transaction {
    private int id;
    private String transactionType;
    private String cryptoName;
    private BigDecimal cryptoAmount;
    private String currency;
    private BigDecimal currencyAmount;
    private Timestamp timestamp;
}
