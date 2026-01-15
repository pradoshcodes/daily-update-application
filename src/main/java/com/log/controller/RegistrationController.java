package com.log.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.log.model.User;
import com.log.repository.UserRepository;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        // Check if username already exists
        if (userRepository.findByName(user.getName()).isPresent()) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        
        // Save user with plain text password (no encoding)
        userRepository.save(user);
        return "redirect:/login";
    }
}