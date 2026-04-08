package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.RoomTypeRequest;
import com.nhom1.hotelmanagement.entities.RoomType;
import com.nhom1.hotelmanagement.services.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/roomtypes")
public class RoomTypeController {

    @Autowired
    private RoomTypeService roomTypeService;

    @GetMapping
    public String listRoomTypes(Model model) {
        model.addAttribute("roomtypes", roomTypeService.listAllDto());
        return "roomtypes";
    }

    @GetMapping("/create")
    public String createRoomTypeForm(Model model) {
        model.addAttribute("roomtype", new RoomTypeRequest());
        return "roomtype-form";
    }

    @PostMapping("/create")
    public String createRoomType(@ModelAttribute RoomTypeRequest dto) {
        roomTypeService.create(dto);
        return "redirect:/roomtypes";
    }

    @GetMapping("/edit/{id}")
    public String editRoomTypeForm(@PathVariable Long id, Model model) {
        RoomType existing = roomTypeService.getById(id);
        if (existing == null) {
            return "redirect:/roomtypes";
        }
        model.addAttribute("roomtype", roomTypeService.toDto(existing));
        return "roomtype-form";
    }

    @PostMapping("/update/{id}")
    public String updateRoomType(@PathVariable Long id, @ModelAttribute RoomTypeRequest dto) {
        roomTypeService.update(id, dto);
        return "redirect:/roomtypes";
    }

    @PostMapping("/delete/{id}")
    public String deleteRoomType(@PathVariable Long id) {
        roomTypeService.delete(id);
        return "redirect:/roomtypes";
    }
}