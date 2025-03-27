package com.cryptotrade.sim.demo.controllers;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptotrade.sim.demo.repos.UsersRepository;
import com.cryptotrade.sim.demo.models.User;

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
}
