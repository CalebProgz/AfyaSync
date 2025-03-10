package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.repository.UserRepository;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import com.example.demo.model.User;
import java.util.Base64;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/api")
public class HomePageController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/HomePage.html")
    public String homePage(Model model, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/api/login";
        }

        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            // Create default user profile
            user = new User();
            user.setEmail(userEmail);
            user.setDefaultValues();
            user = userRepository.save(user);
        }

        // Calculate BMI if height and weight are available
        if (user.getHeight() != null && user.getWeight() != null) {
            double heightInMeters = user.getHeight() / 100.0;
            double bmi = user.getWeight() / (heightInMeters * heightInMeters);
            model.addAttribute("bmi", String.format("%.1f", bmi));
            model.addAttribute("bmiStatus", calculateBMIStatus(bmi));
            // Calculate BMI percentage for progress bar
            double bmiPercentage = calculateBMIPercentage(bmi);
            model.addAttribute("bmiPercentage", bmiPercentage);
        }

        // Convert profile image to base64 if it exists
        if (user.getProfileImage() != null) {
            String base64Image = Base64.getEncoder().encodeToString(user.getProfileImage());
            model.addAttribute("profileImageBase64", base64Image);
        }

        // Format the last login date
        if (user.getLastLogin() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
            String formattedDate = user.getLastLogin().format(formatter);
            model.addAttribute("formattedLastLogin", formattedDate);
        }

        model.addAttribute("user", user);
        return "HomePage";
    }

    private String calculateBMIStatus(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25) return "Normal weight";
        if (bmi < 30) return "Overweight";
        return "Obese";
    }

    private double calculateBMIPercentage(double bmi) {
        // Convert BMI to a percentage (assuming BMI range of 15-40)
        return Math.min(100, Math.max(0, ((bmi - 15) / 25) * 100));
    }

    @GetMapping("/test-connection")
    @ResponseBody
    public String testConnection() {
        try {
            long userCount = userRepository.count();
            return "Successfully connected to database. User count: " + userCount;
        } catch (Exception e) {
            return "Error connecting to database: " + e.getMessage();
        }
    }

    @GetMapping("/test-db")
    @ResponseBody
    public String testDatabase() {
        try {
            long userCount = userRepository.count();
            return "Successfully connected to Railway database. User count: " + userCount;
        } catch (Exception e) {
            return "Error connecting to database: " + e.getMessage();
        }
    }
}