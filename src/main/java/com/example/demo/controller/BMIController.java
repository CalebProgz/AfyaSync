package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class BMIController {

    @GetMapping("/BMI.html")
    public String bmiPage() {
        return "BMI";
    }
}