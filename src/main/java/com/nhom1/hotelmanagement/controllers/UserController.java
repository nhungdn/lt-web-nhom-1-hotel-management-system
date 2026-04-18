package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.SignUpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhom1.hotelmanagement.entities.User;
import com.nhom1.hotelmanagement.services.AuthService;
import com.nhom1.hotelmanagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public String getAllUsers(
            Model model,
            @RequestParam(required = false) Long editId,
            @RequestParam(required = false, defaultValue = "false") boolean updated,
            @RequestParam(required = false, defaultValue = "false") boolean add,
            @RequestParam(required = false, defaultValue = "false") boolean created) {
        boolean addingUser = add && editId == null;
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("editingUser", editId != null ? userService.getUserById(editId) : null);
        model.addAttribute("addingUser", addingUser);
        if (!model.containsAttribute("signupRequest")) {
            model.addAttribute("signupRequest", new SignUpRequest());
        }
        model.addAttribute("roles", User.Role.values());
        model.addAttribute("updated", updated);
        model.addAttribute("created", created);
        model.addAttribute("activePage", "users");
        return "users";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id) {
        return "redirect:/users?editId=" + id;
    }

    @PostMapping("/update/{id}")
    public String updateUser(
            @PathVariable Long id,
            @RequestParam String fullName,
            @RequestParam String phoneNumber,
            @RequestParam User.Role role) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/users";
        }

        user.setFullName(fullName);
        user.setPhoneNumber(phoneNumber);
        user.setRole(role);
        userService.updateUser(user);

        return "redirect:/users?editId=" + id + "&updated=true";
    }

    @PostMapping("/create")
    public String createUserFromView(SignUpRequest request, RedirectAttributes redirectAttributes) {
        try {
            authService.signup(request);
            return "redirect:/users?add=true&created=true";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("signupRequest", request);
            redirectAttributes.addFlashAttribute("createError", ex.getMessage());
            return "redirect:/users?add=true";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteUserFromView(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            userService.deleteUser(user);
        }
        return "redirect:/users";
    }

    @GetMapping("/{username}")
    @ResponseBody
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PostMapping
    @ResponseBody
    public void createUser(@RequestBody User user) {
        userService.createUser(user);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            userService.deleteUser(user);
        }
    }
}
