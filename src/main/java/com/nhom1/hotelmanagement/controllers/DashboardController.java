package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.services.RoomTypeService;
import com.nhom1.hotelmanagement.services.RoomTypeImageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    @Autowired
    private RoomTypeService roomTypeService;
    
    @Autowired
    private RoomTypeImageService roomTypeImageService;
    
    @GetMapping
    public String showDashboard(HttpSession session, Model model) {
        LoginResponse user = (LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        // Pass user info and statistics to dashboard
        model.addAttribute("user", user);
        model.addAttribute("activePage", "dashboard");
        
        // Add room types for sidebar OR any other dashboard data
        model.addAttribute("roomTypes", roomTypeService.listAllDto());
        
        return "dashboard";
    }
}
