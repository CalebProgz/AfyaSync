package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api")
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/EditProfile.html")
    public String editProfilePage(Model model) {
        // TODO: Replace with actual logged-in user ID
        User user = userRepository.findByEmail("mary.ann@gmail.com");
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "EditProfile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam String name,
                              @RequestParam String email,
                              @RequestParam(required = false) String height,
                              @RequestParam(required = false) String weight,
                              @RequestParam(required = false) String age) {
        try {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                user.setName(name);
                if (height != null && !height.isEmpty()) {
                    user.setHeight(Double.parseDouble(height));
                }
                if (weight != null && !weight.isEmpty()) {
                    user.setWeight(Double.parseDouble(weight));
                }
                if (age != null && !age.isEmpty()) {
                    user.setAge(Integer.parseInt(age));
                }
                userRepository.save(user);
                logger.info("Successfully updated user profile for: {}", email);
            }
        } catch (Exception e) {
            logger.error("Error updating profile: ", e);
            return "redirect:/api/EditProfile.html?error=true";
        }
        return "redirect:/api/HomePage.html";
    }
} 