package com.cryptotrade.sim.demo.controllers;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cryptotrade.sim.demo.repos.TransactionsRepository;
import com.cryptotrade.sim.demo.models.Transaction;

@RestController
public class TransactionsController {
    private final TransactionsRepository transactionsRepository;

    public TransactionsController(TransactionsRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
    }

    @GetMapping("/transactions/{id}")
    public List<Transaction> getAllUsersList(@PathVariable int id) {
        return transactionsRepository.getAllUserTransactions(id);
    }
}
