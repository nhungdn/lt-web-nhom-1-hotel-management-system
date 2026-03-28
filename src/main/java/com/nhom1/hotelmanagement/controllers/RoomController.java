package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.RoomRequest;
import com.nhom1.hotelmanagement.entities.Room;
import com.nhom1.hotelmanagement.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public String listRooms(Model model) {
        model.addAttribute("rooms", roomService.listAllDto());
        return "rooms";
    }

    @GetMapping("/create")
    public String createRoomForm(Model model) {
        model.addAttribute("room", new RoomRequest());
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
        model.addAttribute("room", roomService.toDto(existing));
        return "room-form";
    }

    @PostMapping("/update/{id}")
    public String updateRoom(@PathVariable Long id, @ModelAttribute RoomRequest dto) {
        roomService.update(id, dto);
        return "redirect:/rooms";
    }

    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Long id) {
        roomService.delete(id);
        return "redirect:/rooms";
    }

    @GetMapping("/available")
    public String listAvailableRooms(Model model) {
        model.addAttribute("rooms", roomService.listAvailable().stream().map(roomService::toDto).toList());
        return "rooms";
    }
}
