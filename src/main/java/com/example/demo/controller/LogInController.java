package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api")
public class LogInController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/LoginPage.html")
    public String loginPage() {
        return "LoginPage";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        User user = userRepository.findByEmail(email);
        
        if (user != null && user.getPassword().equals(password)) {
            return "redirect:/api/HomePage.html";
        }

        return "redirect:/api/LoginPage.html";
    }
}