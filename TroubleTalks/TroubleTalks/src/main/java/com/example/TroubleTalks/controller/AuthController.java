package com.example.TroubleTalks.controller;

import com.example.TroubleTalks.entity.User;
import com.example.TroubleTalks.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already exists!");
            return "register";
        }
        userService.registerUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/about")
    public String about() {
        return "about"; // resolves to about.html
    }

    @GetMapping("/welcome")
    public String welcome(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        model.addAttribute("user", user);
        return "welcome";
    }
}
