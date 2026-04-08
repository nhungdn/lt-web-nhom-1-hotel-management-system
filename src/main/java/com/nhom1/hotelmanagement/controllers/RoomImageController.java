package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.RoomImageRequest;
import com.nhom1.hotelmanagement.entities.RoomImage;
import com.nhom1.hotelmanagement.services.RoomImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/roomimages")
public class RoomImageController {

    @Autowired
    private RoomImageService roomImageService;

    @GetMapping
    public String listRoomImages(Model model) {
        model.addAttribute("roomimages", roomImageService.listAllDto());
        return "roomimages";
    }

    @GetMapping("/create")
    public String createRoomImageForm(Model model) {
        model.addAttribute("roomimage", new RoomImageRequest());
        return "roomimage-form";
    }

    @PostMapping("/create")
    public String createRoomImage(@ModelAttribute RoomImageRequest dto) {
        roomImageService.create(dto);
        return "redirect:/roomimages";
    }

    @GetMapping("/edit/{id}")
    public String editRoomImageForm(@PathVariable Long id, Model model) {
        RoomImage existing = roomImageService.getById(id);
        if (existing == null) {
            return "redirect:/roomimages";
        }
        model.addAttribute("roomimage", roomImageService.toDto(existing));
        return "roomimage-form";
    }

    @PostMapping("/update/{id}")
    public String updateRoomImage(@PathVariable Long id, @ModelAttribute RoomImageRequest dto) {
        roomImageService.update(id, dto);
        return "redirect:/roomimages";
    }

    @PostMapping("/delete/{id}")
    public String deleteRoomImage(@PathVariable Long id) {
        roomImageService.delete(id);
        return "redirect:/roomimages";
    }

    @GetMapping("/room/{roomId}")
    public String listRoomImagesByRoom(@PathVariable Long roomId, Model model) {
        model.addAttribute("roomimages", roomImageService.listByRoomId(roomId).stream().map(roomImageService::toDto).toList());
        return "roomimages";
    }
}