package com.nhom1.hotelmanagement.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nhom1.hotelmanagement.entities.User;
import com.nhom1.hotelmanagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("activePage", "users");
        return "users";
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
