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
    private String transaction_type;
    private String crypto_name;
    private BigDecimal crypto_amount;
    private String currency;
    private BigDecimal currency_amount;
    private Timestamp timestamp;
}
