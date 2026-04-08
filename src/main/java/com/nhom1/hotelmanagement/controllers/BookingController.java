
package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.entities.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


public class BookingController {
    @Autowired private BookingService bookingService;
    
    @GetMapping("/booking")
    public String showRoomStat(HttpSession session){
        LoginResponse user = (LoginResponse) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        
        return "roomstat";
    }
            
    @PostMapping("/booking/edit")
    public String editRoomStat(){
        return "roomstat";
    }
    
    @PostMapping("/booking/cancel")
    public String cancelRoomStat(){
        return "roomstat";
    }
    
    @PostMapping("/booking/delete")
    public String deleteRoomStat(){
        return "roomstat";
    }
    
}
