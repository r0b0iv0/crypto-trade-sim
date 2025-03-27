package com.cryptotrade.sim.demo.models;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter


public class User {
    private int id;
    private String name;
    private BigDecimal balance;  
    
}
