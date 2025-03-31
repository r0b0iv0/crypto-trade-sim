package com.cryptotrade.sim.demo.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.*;

@RestController
public class KrakenController {

    private static final Map<String, String> SYMBOL_MAP = Map.ofEntries(
        Map.entry("XBT", "XXBTZUSD"),
        Map.entry("ETH", "XETHZUSD"),
        Map.entry("USDT", "USDTZUSD"),
        Map.entry("XRP", "XXRPZUSD"),
        Map.entry("SOL", "SOLUSD"),
        Map.entry("USDC", "USDCUSD"),
        Map.entry("XDG", "XDGUSD"),
        Map.entry("ADA", "ADAUSD"),
        Map.entry("TRX", "TRXUSD"),
        Map.entry("WBTC", "WBTCUSD"),
        Map.entry("DOT", "DOTUSD"),
        Map.entry("LTC", "XLTCZUSD"),
        Map.entry("LINK", "LINKUSD"),
        Map.entry("XLM", "XXLMZUSD"),
        Map.entry("UNI", "UNIUSD"),
        Map.entry("AVAX", "AVAXUSD"),
        Map.entry("SHIB", "SHIBUSD"),
        Map.entry("SUI", "SUIUSD"),
        Map.entry("ATOM", "ATOMUSD")
    );
    


    @GetMapping("/prices")
    public Map<String, BigDecimal> getPrices() {
        Map<String, BigDecimal> prices = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();
        

        String pairs = String.join(",", SYMBOL_MAP.values());
        String url = "https://api.kraken.com/0/public/Ticker?pair=" + pairs;
        

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        Map<String, Object> result = (Map<String, Object>) response.get("result");
        

        SYMBOL_MAP.forEach((symbol, krakenSymbol) -> {
            Map<String, Object> ticker = (Map<String, Object>) result.get(krakenSymbol);
            if (ticker != null) {

                Object priceData = ticker.get("c");
                String price;
                
                if (priceData instanceof List) {

                    price = ((List<?>) priceData).get(0).toString();
                } else if (priceData instanceof String[]) {

                    price = ((String[]) priceData)[0];
                } else {
                    throw new RuntimeException("Unexpected price data type");
                }
                
                prices.put(symbol, new BigDecimal(price));
            }
        });
        System.out.println(prices);
        return prices;
    }
}