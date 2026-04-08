package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.LoginRequest;
import com.nhom1.hotelmanagement.dto.SignUpRequest;
import com.nhom1.hotelmanagement.services.AuthService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String showLogin(Model model,
                            @RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout) {
        LoginRequest loginRequest = new LoginRequest();
        model.addAttribute("loginRequest", loginRequest);
        model.addAttribute("loginError", error != null);
        model.addAttribute("logoutSuccess", logout != null);
        return "login";
    }

    @GetMapping("/signup")
    public String showSignup(Model model,
                             @RequestParam(value = "created", required = false) String created) {
        if (!model.containsAttribute("signupRequest")) {
            model.addAttribute("signupRequest", new SignUpRequest());
        }
        model.addAttribute("createdSuccess", created != null);
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute SignUpRequest request, Model model) {
        try {
            authService.signup(request);
            return "redirect:/signup?created=true";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("signupRequest", request);
            model.addAttribute("signupError", ex.getMessage());
            model.addAttribute("createdSuccess", false);
            return "signup";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}