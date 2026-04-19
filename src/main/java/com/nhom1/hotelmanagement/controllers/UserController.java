package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.SignUpRequest;
import com.nhom1.hotelmanagement.entities.User;
import com.nhom1.hotelmanagement.services.AuthService;
import com.nhom1.hotelmanagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final String DEFAULT_RESET_PASSWORD = "123456";

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Hiển thị danh sách users
    @GetMapping
    public String getAllUsers(
            Model model,
            @RequestParam(required = false) Long editId,
            @RequestParam(required = false, defaultValue = "false") boolean updated,
            @RequestParam(required = false, defaultValue = "false") boolean passwordReset,
            @RequestParam(required = false, defaultValue = "false") boolean add,
            @RequestParam(required = false, defaultValue = "false") boolean created) {

        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", User.Role.values());
        model.addAttribute("activePage", "users");

        // Trạng thái thông báo
        model.addAttribute("updated", updated);
        model.addAttribute("passwordReset", passwordReset);
        model.addAttribute("created", created);

        // Logic hiển thị Panel chỉnh sửa hoặc thêm mới ở bên phải
        model.addAttribute("addingUser", add && editId == null);
        model.addAttribute("editingUser", editId != null ? userService.getUserById(editId) : null);

        if (!model.containsAttribute("signupRequest")) {
            model.addAttribute("signupRequest", new SignUpRequest());
        }

        return "users";
    }

    // Cập nhật thông tin tài khoản
    @PostMapping("/update/{id}")
    public String updateUser(
            @PathVariable Long id,
            @RequestParam String fullName,
            @RequestParam String phoneNumber,
            @RequestParam User.Role role) {

        User user = userService.getUserById(id);
        if (user != null) {
            user.setFullName(fullName);
            user.setPhoneNumber(phoneNumber);
            user.setRole(role);
            userService.updateUser(user);
        }
        return "redirect:/users?editId=" + id + "&updated=true";
    }

    // Reset mật khẩu về mặc định
    @PostMapping("/reset-password/{id}")
    public String resetPassword(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(DEFAULT_RESET_PASSWORD));
            userService.updateUser(user);
        }
        return "redirect:/users?editId=" + id + "&passwordReset=true";
    }

    // Tạo tài khoản mới
    @PostMapping("/create")
    public String createUser(SignUpRequest request, RedirectAttributes redirectAttributes) {
        try {
            authService.signup(request);
            return "redirect:/users?add=true&created=true";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("signupRequest", request);
            redirectAttributes.addFlashAttribute("createError", ex.getMessage());
            return "redirect:/users?add=true";
        }
    }

    // Xóa tài khoản
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            userService.deleteUser(user);
        }
        return "redirect:/users";
    }
}