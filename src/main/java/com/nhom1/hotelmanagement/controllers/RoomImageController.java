package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.RoomImageRequest;
import com.nhom1.hotelmanagement.dto.RoomImageResponse;
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
        if (dto.getRoomId() != null) {
            return "redirect:/rooms/" + dto.getRoomId() + "/images";
        }
        return "redirect:/roomimages";
    }

    @GetMapping("/edit/{id}")
    public String editRoomImageForm(@PathVariable Long id, @RequestParam(required = false) String returnUrl, Model model) {
        RoomImage existing = roomImageService.getById(id);
        if (existing == null) {
            return "redirect:/roomimages";
        }
        // Convert RoomImageResponse to RoomImageRequest for form binding
        RoomImageResponse response = roomImageService.toDto(existing);
        RoomImageRequest request = new RoomImageRequest();
        request.setRoomImageId(response.getRoomImageId());
        request.setImageUrl(response.getImageUrl());
        request.setDescription(response.getDescription());
        request.setRoomId(response.getRoomId());
        
        model.addAttribute("roomimage", request);
        model.addAttribute("roomId", existing.getRoom() != null ? existing.getRoom().getRoomId() : null);
        if (returnUrl != null && !returnUrl.isEmpty()) {
            model.addAttribute("returnUrl", returnUrl);
        }
        return "roomimage-form";
    }

    @PostMapping("/update/{id}")
    public String updateRoomImage(@PathVariable Long id, @ModelAttribute RoomImageRequest dto, @RequestParam(required = false) String returnUrl) {
        roomImageService.update(id, dto);
        
        // If returnUrl is provided, redirect there
        if (returnUrl != null && !returnUrl.isEmpty()) {
            return "redirect:" + returnUrl;
        }
        
        if (dto.getRoomId() != null) {
            return "redirect:/rooms/" + dto.getRoomId() + "/images";
        }
        return "redirect:/roomimages";
    }

    @PostMapping("/delete/{id}")
    public String deleteRoomImage(@PathVariable Long id, @RequestParam(required = false) String returnUrl) {
        RoomImage roomImage = roomImageService.getById(id);
        roomImageService.delete(id);
        
        // If returnUrl is provided, redirect there
        if (returnUrl != null && !returnUrl.isEmpty()) {
            return "redirect:" + returnUrl;
        }
        
        // If image had a room, redirect to room images page
        if (roomImage != null && roomImage.getRoom() != null) {
            return "redirect:/rooms/" + roomImage.getRoom().getRoomId() + "/images";
        }
        
        return "redirect:/roomimages";
    }

    @GetMapping("/room/{roomId}")
    public String listRoomImagesByRoom(@PathVariable Long roomId, Model model) {
        model.addAttribute("roomimages", roomImageService.listByRoomId(roomId).stream().map(roomImageService::toDto).toList());
        return "roomimages";
    }
}