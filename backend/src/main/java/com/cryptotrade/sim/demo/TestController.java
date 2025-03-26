package com.cryptotrade.sim.demo;

import org.springframework.web.bind.annotation.RestController;

import com.cryptotrade.sim.demo.models.User;
import com.cryptotrade.sim.demo.repos.UsersRepository;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class TestController {
    private final UsersRepository usersRepository;

    public TestController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }
    
    @GetMapping("/")
    public List<User> getMethodName() {
        return usersRepository.getAllUsersList();
    }
    
}
