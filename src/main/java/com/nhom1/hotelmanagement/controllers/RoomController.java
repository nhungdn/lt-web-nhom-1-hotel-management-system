/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nhom1.hotelmanagement.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.dto.RoomStatDTO;
import com.nhom1.hotelmanagement.entities.Room;
import com.nhom1.hotelmanagement.services.RoomService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class RoomController {
    @Autowired RoomService roomService;
    @GetMapping("/room-status")
    public String showRoomStat(HttpSession session, Model model){
        LoginResponse user = (LoginResponse) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        
        List<RoomStatDTO> roomlist = roomService.getFullRoomList();
        try {
            ObjectMapper mapper = new ObjectMapper();
            String roomListJson = mapper.writeValueAsString(roomlist);
            model.addAttribute("roomList", roomListJson);    
            
        } catch (JsonProcessingException e) {
            model.addAttribute("roomList", "[]");
        }
        return "roomstat";
    }
}
