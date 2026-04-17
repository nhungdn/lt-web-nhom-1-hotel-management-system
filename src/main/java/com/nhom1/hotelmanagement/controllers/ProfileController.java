package com.nhom1.hotelmanagement.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.entities.User;
import com.nhom1.hotelmanagement.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showProfile(
            Authentication authentication,
            Model model,
            @RequestParam(required = false, defaultValue = "false") boolean updated,
            @RequestParam(required = false, defaultValue = "false") boolean passwordUpdated,
            @RequestParam(required = false) String error) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("updated", updated);
        model.addAttribute("passwordUpdated", passwordUpdated);
        model.addAttribute("error", error);
        model.addAttribute("activePage", "profile");
        return "user-profile";
    }

    @PostMapping("/update")
    public String updateProfile(
            Authentication authentication,
            HttpSession session,
            @RequestParam String currentPassword,
            @RequestParam String fullName,
            @RequestParam String phoneNumber,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!StringUtils.hasText(currentPassword)
                || !passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
            return "redirect:/profile?error=current_password_invalid";
        }

        currentUser.setFullName(fullName);
        currentUser.setPhoneNumber(phoneNumber);

        boolean hasNewPassword = StringUtils.hasText(newPassword);
        boolean hasConfirmPassword = StringUtils.hasText(confirmPassword);

        if (hasNewPassword || hasConfirmPassword) {
            if (!hasNewPassword || !hasConfirmPassword || !newPassword.equals(confirmPassword)) {
                return "redirect:/profile?error=password_mismatch";
            }
            currentUser.setPassword(passwordEncoder.encode(newPassword));
        }

        userService.updateUser(currentUser);
        session.setAttribute("user", new LoginResponse(
                currentUser.getUserId(),
                currentUser.getUsername(),
                currentUser.getFullName(),
                currentUser.getRole()));

        return "redirect:/profile?updated=true&passwordUpdated=" + hasNewPassword;
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return userService.getUserByUsername(authentication.getName());
    }
}