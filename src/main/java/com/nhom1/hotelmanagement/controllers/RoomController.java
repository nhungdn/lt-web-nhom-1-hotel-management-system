package com.nhom1.hotelmanagement.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.dto.RoomRequest;
import com.nhom1.hotelmanagement.dto.RoomImageRequest;
import com.nhom1.hotelmanagement.dto.RoomStatDTO;
import com.nhom1.hotelmanagement.entities.Room;
import com.nhom1.hotelmanagement.services.RoomService;
import com.nhom1.hotelmanagement.services.RoomTypeService;
import com.nhom1.hotelmanagement.services.RoomImageService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomTypeService roomTypeService;

    @Autowired
    private RoomImageService roomImageService;

    @GetMapping
    public String listRooms(Model model) {
        model.addAttribute("rooms", roomService.listAllDto());
        return "rooms";
    }

    @GetMapping("/create")
    public String createRoomForm(Model model) {
        RoomRequest roomRequest = new RoomRequest();
        roomRequest.setImages(new java.util.ArrayList<>());
        model.addAttribute("room", roomRequest);
        model.addAttribute("roomTypes", roomTypeService.listAllDto());
        return "room-form";
    }

    @PostMapping("/create")
    public String createRoom(@ModelAttribute RoomRequest dto) {
        roomService.create(dto);
        return "redirect:/rooms";
    }

    @GetMapping("/edit/{id}")
    public String editRoomForm(@PathVariable Long id, Model model) {
        Room existing = roomService.getById(id);
        if (existing == null) {
            return "redirect:/rooms";
        }
        // Convert Room entity to RoomRequest for form binding
        RoomRequest roomRequest = new RoomRequest();
        roomRequest.setRoomId(existing.getRoomId());
        roomRequest.setRoomNumber(existing.getRoomNumber());
        roomRequest.setStatus(existing.getStatus() == null ? null : existing.getStatus().name());
        roomRequest.setRoomTypeId(existing.getRoomType() == null ? null : existing.getRoomType().getRoomTypeId());
        roomRequest.setImages(new java.util.ArrayList<>()); // Initialize empty images list for edit mode
        
        model.addAttribute("room", roomRequest);
        model.addAttribute("roomTypes", roomTypeService.listAllDto());
        model.addAttribute("roomImages", roomImageService.listByRoomId(id).stream().map(roomImageService::toDto).toList());
        return "room-form";
    }

    @PostMapping("/update/{id}")
    public String updateRoom(@PathVariable Long id, @ModelAttribute RoomRequest dto) {
        roomService.update(id, dto);
        return "redirect:/rooms";
    }

    @PostMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Long id) {
        roomService.delete(id);
        return "redirect:/rooms";
    }

    @GetMapping("/{id}/images")
    public String viewRoomImages(@PathVariable Long id, Model model) {
        Room room = roomService.getById(id);
        if (room == null) {
            return "redirect:/rooms";
        }
        model.addAttribute("room", roomService.toDto(room));
        model.addAttribute("roomImages", roomImageService.listByRoomId(id).stream().map(roomImageService::toDto).toList());
        return "roomimages";
    }

    @GetMapping("/{id}/images/add")
    public String addRoomImageForm(@PathVariable Long id, Model model) {
        Room room = roomService.getById(id);
        if (room == null) {
            return "redirect:/rooms";
        }
        RoomImageRequest imageRequest = new RoomImageRequest();
        imageRequest.setRoomId(id);
        model.addAttribute("roomimage", imageRequest);
        model.addAttribute("roomId", id);
        model.addAttribute("returnUrl", "/rooms/" + id + "/edit");
        return "roomimage-form";
    }

    @GetMapping("/available")
    public String listAvailableRooms(Model model) {
        model.addAttribute("rooms", roomService.listAvailable().stream().map(roomService::toDto).toList());
        return "rooms";
    }
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

