
package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.LoginResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String showHome(Model model, HttpSession session){
        // Kiem tra user
        LoginResponse user = (LoginResponse) session.getAttribute("user");

        if (user != null) {
            model.addAttribute("user", user);
            
            return "index"; // co -> cho truy cap
        }
        
        return "redirect:/login"; // Chua -> Login
    }
}
