package com.log.controller;

import com.log.model.User;
import com.log.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        
        // Search database for user with matching username and password
        Optional<User> userOpt = userRepository.findByNameAndPassword(username, password);
        
        if (userOpt.isPresent()) {
            // User found - create session and redirect to calendar
            User user = userOpt.get();
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getName());
            return "redirect:/calendar";
        } else {
            // User not found - show error message
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}