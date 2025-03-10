package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api")
public class SignUpController {
    private static final Logger logger = LoggerFactory.getLogger(SignUpController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/SignUp.html")
    public String signUp() {
        return "SignUp";
    }

    @PostMapping("/signup")
    public String signUp(@RequestParam String name, @RequestParam String email, @RequestParam String password) {
        try {
            logger.info("Attempting to create new user with email: {}", email);
            
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            
            User savedUser = userRepository.save(user);
            logger.info("Successfully created user with ID: {}", savedUser.getId());
            
            return "redirect:/api/login";
        } catch (Exception e) {
            logger.error("Error creating user: ", e);
            return "redirect:/api/SignUp.html?error=true";
        }
    }
}

    