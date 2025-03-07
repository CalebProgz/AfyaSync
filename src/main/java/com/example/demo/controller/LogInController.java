package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api")
public class LogInController {

    @GetMapping("/LoginPage.html")
    public String loginPage() {
        return "LoginPage";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        if(email.equals("admin@zuri.com") && password.equals("password123")) {
            return "redirect:/api/HomePage.html";
        }

        return "redirect:/api/LoginPage.html";
    }
}