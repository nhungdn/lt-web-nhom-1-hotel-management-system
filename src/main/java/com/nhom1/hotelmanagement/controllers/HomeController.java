
package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.services.RoomTypeService;
import com.nhom1.hotelmanagement.services.RoomTypeImageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;
import java.util.HashMap;

@Controller
public class HomeController {
    
    @Autowired
    private RoomTypeService roomTypeService;
    
    @Autowired
    private RoomTypeImageService roomTypeImageService;
    
    @GetMapping("/")
    public String showHome(HttpSession session, Model model) {
        // Pass room types and their images to homepage
        var roomTypes = roomTypeService.listAllDto();
        model.addAttribute("roomTypes", roomTypes);
        
        // Load images for each room type
        Map<Long, Object> roomTypeImages = new HashMap<>();
        roomTypes.forEach(rt -> {
            roomTypeImages.put(rt.getRoomTypeId(), roomTypeImageService.listByRoomTypeId(rt.getRoomTypeId()));
        });
        model.addAttribute("roomTypeImages", roomTypeImages);
        
        // Check if user is logged in
        LoginResponse user = (LoginResponse) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("isLoggedIn", true);
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        
        return "index";
    }
}

