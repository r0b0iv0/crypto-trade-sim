package com.cryptotrade.sim.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cryptotrade.sim.demo.repos.UsersRepository;
import com.cryptotrade.sim.demo.models.BuyCryptoRequest;
import com.cryptotrade.sim.demo.models.SellCryptoRequest;
import com.cryptotrade.sim.demo.models.UserPortfolio;

@RestController
public class UsersController {
    private final UsersRepository usersRepository;

    public UsersController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @GetMapping("/user/{userId}")
    public UserPortfolio getUserPortfolio(@PathVariable int userId) {
        return usersRepository.getUserPortfolio(userId);
    }

    @PostMapping("/buy")
    public ResponseEntity<String> buyCrypto(@RequestBody BuyCryptoRequest request) {
        try {
            return ResponseEntity.ok(usersRepository.userBuysCrypto(request.getUserId(), request.getCryptoSymbol(),
                    request.getCryptoAmount(),
                    request.getPrice()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());

        }
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sellCrypto(
            @RequestBody SellCryptoRequest request) {
        try {
            return ResponseEntity.ok(usersRepository.sellCrypto(
                    request.getUserId(),
                    request.getCryptoSymbol(),
                    request.getAmount(),
                    request.getUsdPrice()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/reset/{userId}")
    public ResponseEntity<String> resetUser(@PathVariable int userId) {
        try {
            usersRepository.resetUserData(userId);
            return ResponseEntity.ok("User data reset successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

    }

}
