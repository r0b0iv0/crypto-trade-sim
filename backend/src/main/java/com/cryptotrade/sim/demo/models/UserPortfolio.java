package com.cryptotrade.sim.demo.models;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

import java.util.ArrayList;


@Data
public class UserPortfolio {
    private String name;
    private BigDecimal balance;
    private List<Holding> holdings = new ArrayList<>();

    public record Holding(String cryptoSymbol, BigDecimal amount) {}
}
