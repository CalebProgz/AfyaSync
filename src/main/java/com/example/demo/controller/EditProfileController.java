package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import com.example.demo.repository.UserRepository;
import com.example.demo.model.User;
import java.time.LocalDateTime;

@Controller
public class EditProfileController {
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/api/EditProfile.html")
    public String showEditProfile(Model model, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/api/login";
        }
        
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            return "redirect:/api/HomePage.html"; // Redirect to homepage to create default profile
        }
        
        model.addAttribute("user", user);
        return "EditProfile";
    }
    
    @PostMapping("/api/updateProfile")
    public String updateProfile(@ModelAttribute User updatedUser, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/api/login";
        }
        
        User existingUser = userRepository.findByEmail(userEmail);
        if (existingUser != null) {
            // Update user details while preserving some fields
            existingUser.setName(updatedUser.getName());
            existingUser.setHeight(updatedUser.getHeight());
            existingUser.setWeight(updatedUser.getWeight());
            existingUser.setAge(updatedUser.getAge());
            existingUser.setLastLogin(LocalDateTime.now());
            
            // Validate input values
            if (existingUser.getHeight() != null && (existingUser.getHeight() < 1 || existingUser.getHeight() > 300)) {
                existingUser.setHeight(170.0); // Reset to default if invalid
            }
            if (existingUser.getWeight() != null && (existingUser.getWeight() < 1 || existingUser.getWeight() > 500)) {
                existingUser.setWeight(70.0); // Reset to default if invalid
            }
            if (existingUser.getAge() != null && (existingUser.getAge() < 1 || existingUser.getAge() > 150)) {
                existingUser.setAge(25); // Reset to default if invalid
            }
            
            userRepository.save(existingUser);
        }
        
        return "redirect:/api/HomePage.html";
    }
} 