package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.SignUpRequest;
import com.nhom1.hotelmanagement.entities.User;
import com.nhom1.hotelmanagement.services.AuthService;
import com.nhom1.hotelmanagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    // Hiển thị danh sách users
    @GetMapping
    public String getAllUsers(
            Model model,
            @RequestParam(required = false) Long editId,
            @RequestParam(required = false) String searchUsername,
            @RequestParam(required = false, defaultValue = "false") boolean updated,
            @RequestParam(required = false, defaultValue = "false") boolean passwordReset,
            @RequestParam(required = false, defaultValue = "false") boolean add,
            @RequestParam(required = false, defaultValue = "false") boolean created) {

        model.addAttribute("users", userService.searchUsersByUsername(searchUsername));
        model.addAttribute("roles", User.Role.values());
        model.addAttribute("activePage", "users");
        model.addAttribute("searchUsername", searchUsername);

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
            @RequestParam User.Role role,
            @RequestParam(required = false) String searchUsername) {

        userService.updateUser(id, fullName, phoneNumber, role);

        return "redirect:/users?editId=" + id + "&updated=true" + (searchUsername != null && !searchUsername.isBlank() ? "&searchUsername=" + searchUsername : "");
    }

    // Reset mật khẩu về mặc định
    @PostMapping("/reset-password/{id}")
    public String resetPassword(
            @PathVariable Long id,
            @RequestParam(required = false) String searchUsername) {

        userService.resetToDefaultPassword(id);

        return "redirect:/users?editId=" + id + "&passwordReset=true" + (searchUsername != null && !searchUsername.isBlank() ? "&searchUsername=" + searchUsername : "");
    }

    // Tạo tài khoản mới
    @PostMapping("/create")
    public String createUser(
            SignUpRequest request,
            @RequestParam(required = false) String searchUsername,
            RedirectAttributes redirectAttributes) {
        try {
            authService.signup(request);
            return "redirect:/users?add=true&created=true"
                    + (searchUsername != null && !searchUsername.isBlank() ? "&searchUsername=" + searchUsername : "");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("signupRequest", request);
            redirectAttributes.addFlashAttribute("createError", ex.getMessage());
            return "redirect:/users?add=true" + (searchUsername != null && !searchUsername.isBlank() ? "&searchUsername=" + searchUsername : "");
        }
    }

    // Xóa tài khoản
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, @RequestParam(required = false) String searchUsername) {

        userService.deleteUser(id);

        return "redirect:/users" + (searchUsername != null && !searchUsername.isBlank() ? "?searchUsername=" + searchUsername : "");
    }
}