package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.dto.ProfileUpdateRequest;
import com.nhom1.hotelmanagement.entities.User;
import com.nhom1.hotelmanagement.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showProfile(Authentication authentication, Model model) {
        if (authentication == null)
            return "redirect:/login";

        User currentUser = userService.getUserByUsername(authentication.getName());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activePage", "profile");

        return "user-profile";
    }

    @PostMapping("/update")
    public String updateProfile(
            Authentication authentication,
            HttpSession session,
            @ModelAttribute ProfileUpdateRequest request,
            RedirectAttributes redirectAttributes) {

        try {
            userService.updateProfile(authentication.getName(), request);

            // Cập nhật lại thông tin user trong Session khi thành công
            User updatedUser = userService.getUserByUsername(authentication.getName());
            session.setAttribute("user", new LoginResponse(
                    updatedUser.getUserId(),
                    updatedUser.getUsername(),
                    updatedUser.getFullName(),
                    updatedUser.getRole()));

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công!");
            return "redirect:/profile";

        } catch (RuntimeException e) {
            // Đẩy dữ liệu cũ vào Flash Attribute để hiển thị lại trên form
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("oldData", request);

            return "redirect:/profile";
        }
    }
}