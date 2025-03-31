package com.cryptotrade.sim.demo.helpers;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.util.Arrays;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class KrakenWebSocketClient extends WebSocketClient {
    private static final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();


    public KrakenWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Opened WebSocket connection.");

        JSONObject subscriptionMessage = new JSONObject();
        subscriptionMessage.put("event", "subscribe");
        subscriptionMessage.put("pair", new JSONArray(Arrays.asList("BTC/USD", "ETH/USD", "USDT/USD", "XRP/USD", "SOL/USD", "USDC/USD", "DOGE/USD", "ADA/USD", "TRX/USD", "WBTC/USD",  
"DOT/USD", "LTC/USD", "LINK/USD", "XLM/USD", "UNI/USD", "AVAX/USD", "SHIB/USD", "SUI/USD", "ATOM/USD"))); 
        


        JSONObject subscriptionDetails = new JSONObject();
        subscriptionDetails.put("name", "ticker");

        subscriptionMessage.put("subscription", subscriptionDetails);

        this.send(subscriptionMessage.toString());
    }

    @Override
    public void onMessage(String message) {

        try {
            if (message.startsWith("[")) {
                JSONArray responseArray = new JSONArray(message);
                    String tradingPair = responseArray.getString(3);
                    String formatedCurrencyName = tradingPair.substring(0, tradingPair.length()-4);

                    JSONObject tickerData = responseArray.getJSONObject(1);
                    JSONArray lastTrade = tickerData.getJSONArray("c");
                    String latestPrice = lastTrade.getString(0);
                    // System.out.println("The price of " + formatedCurrencyName + " is: " + latestPrice);
                    String jsonResponse = "{\"symbol\": \"" + formatedCurrencyName + "\", \"price\": \"" + latestPrice + "\"}";

                for (WebSocketSession session : sessions) {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(jsonResponse));
                    }
                }

            } else {
                JSONObject jsonResponse = new JSONObject(message);
                if (jsonResponse.has("event") && jsonResponse.getString("event").equals("subscriptionStatus")) {
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Closed WebSocket connection. Code: " + code + ", Reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public static void registerSession(WebSocketSession session) {
        sessions.add(session);
    }

    public static void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }

}
