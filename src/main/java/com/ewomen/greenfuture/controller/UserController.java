package com.ewomen.greenfuture.controller;

import com.ewomen.greenfuture.entity.User;
import com.ewomen.greenfuture.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }
}