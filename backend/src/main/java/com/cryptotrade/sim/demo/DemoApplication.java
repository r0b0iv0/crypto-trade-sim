package com.cryptotrade.sim.demo;

import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cryptotrade.sim.demo.helpers.KrakenWebSocketClient;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		try {
            URI serverUri = new URI("wss://ws.kraken.com"); 
            KrakenWebSocketClient client = new KrakenWebSocketClient(serverUri);
            client.connect();
        } catch (Exception e) {
            // e.printStackTrace();
        }
		SpringApplication.run(DemoApplication.class, args);
	}

}
