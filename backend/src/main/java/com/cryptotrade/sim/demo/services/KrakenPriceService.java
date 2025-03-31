package com.cryptotrade.sim.demo.services;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class KrakenPriceService {

    private static final String API_URL = "https://api.kraken.com/0/public/Ticker?pair=BTCUSD,ETHUSD,USDTUSD,XRPUSD,SOLUSD,USDCUSD,DOGEUSD,ADAUSD,TRXUSD,WBTCUSD,DOTUSD,LTCUSD,LINKUSD,XLMUSD,UNIUSD,BNBUSD,AVAXUSD,SHIBUSD,MATICUSD,ATOMUSD";

    public Map<String, String> fetchPrices() {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(API_URL, String.class);

        JSONObject json = new JSONObject(response).getJSONObject("result");
        Map<String, String> prices = new HashMap<>();

        for (String key : json.keySet()) {
            String lastPrice = json.getJSONObject(key).getJSONArray("c").getString(0);
            prices.put(key, lastPrice);
        }
        return prices;
    }
}
