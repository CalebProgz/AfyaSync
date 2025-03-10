package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/api")
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/uploadProfilePicture")
    @ResponseBody
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file,
                                                HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Starting profile picture upload process");
            
            String userEmail = (String) session.getAttribute("userEmail");
            logger.debug("User email from session: {}", userEmail);
            
            if (userEmail == null) {
                logger.warn("Upload attempt with no user session");
                response.put("success", false);
                response.put("message", "User not logged in");
                return ResponseEntity.badRequest().body(response);
            }

            User user = userRepository.findByEmail(userEmail);
            if (user == null) {
                logger.warn("No user found for email: {}", userEmail);
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }

            logger.debug("File size: {} bytes", file.getSize());
            if (file.getSize() > 5 * 1024 * 1024) {
                logger.warn("File size too large: {} bytes", file.getSize());
                response.put("success", false);
                response.put("message", "File size exceeds 5MB limit");
                return ResponseEntity.badRequest().body(response);
            }

            String contentType = file.getContentType();
            logger.debug("File content type: {}", contentType);
            if (contentType == null || !contentType.startsWith("image/")) {
                logger.warn("Invalid content type: {}", contentType);
                response.put("success", false);
                response.put("message", "Only image files are allowed");
                return ResponseEntity.badRequest().body(response);
            }

            byte[] imageData = file.getBytes();
            user.setProfileImage(imageData);
            userRepository.save(user);
            logger.info("Successfully saved profile picture for user: {}", userEmail);

            String base64Image = Base64.getEncoder().encodeToString(imageData);
            response.put("success", true);
            response.put("imageData", "data:" + contentType + ";base64," + base64Image);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            logger.error("Failed to process image upload", e);
            response.put("success", false);
            response.put("message", "Failed to process image: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        } catch (Exception e) {
            logger.error("Unexpected error during image upload", e);
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/profilePicture")
    @ResponseBody
    public ResponseEntity<?> getProfilePicture(HttpSession session) {
        try {
            String userEmail = (String) session.getAttribute("userEmail");
            if (userEmail == null) {
                logger.warn("Profile picture request with no user session");
                return ResponseEntity.badRequest().body(Map.of("success", false, 
                    "message", "User not logged in"));
            }

            User user = userRepository.findByEmail(userEmail);
            if (user == null || user.getProfileImage() == null) {
                logger.warn("No profile picture found for user: {}", userEmail);
                return ResponseEntity.badRequest().body(Map.of("success", false, 
                    "message", "No profile picture found"));
            }

            String base64Image = Base64.getEncoder().encodeToString(user.getProfileImage());
            return ResponseEntity.ok(Map.of("success", true, 
                "imageData", "data:image/jpeg;base64," + base64Image));
        } catch (Exception e) {
            logger.error("Error retrieving profile picture", e);
            return ResponseEntity.internalServerError().body(Map.of("success", false,
                "message", "Failed to retrieve profile picture"));
        }
    }
} 