package com.cryptotrade.sim.demo.helpers;

import java.math.BigDecimal;

public class Validator {
    
    public void checkIfBigDecimal(BigDecimal value, String errorMessage) {
        if (!(value instanceof BigDecimal) || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(errorMessage);
        }
    }
}
