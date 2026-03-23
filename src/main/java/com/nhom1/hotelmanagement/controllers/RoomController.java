/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.LoginResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class RoomController {
   
    @GetMapping("/room-status")
    public String showRoomStat(HttpSession session){
        LoginResponse user = (LoginResponse) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        
        return "roomstat";
    }
}
