package com.cryptotrade.sim.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.cryptotrade.sim.demo.repos.UsersRepository;
import com.cryptotrade.sim.demo.models.BuyCryptoRequest;
import com.cryptotrade.sim.demo.models.SellCryptoRequest;
import com.cryptotrade.sim.demo.models.User;
import java.math.BigDecimal;

@RestController
public class UsersController {
    private final UsersRepository usersRepository;

    public UsersController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @GetMapping("/users")
    public List<User> getAllUsersList() {
        return usersRepository.getAllUsersList();
    }

    @GetMapping("/user/{id}")
    public User getSingleUser(@PathVariable int id) {
        return usersRepository.getSingleUser(id);
    }

    @PostMapping("/buy")
    public ResponseEntity<String> buyCrypto(@RequestBody BuyCryptoRequest request) {
        try {
            usersRepository.userBuysCrypto(request.getUserId(), request.getCryptoSymbol(), request.getCryptoAmount(), request.getPrice());
            return ResponseEntity.ok("Purchase successful!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            
        }
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sellCrypto(
            @RequestBody SellCryptoRequest request) {
        try {
            int userId = 1;
            usersRepository.sellCrypto(
                userId,
                request.getCryptoSymbol(),
                request.getAmount(),
                request.getUsdPrice()
            );
            return ResponseEntity.ok("Sell succesfull");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
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


