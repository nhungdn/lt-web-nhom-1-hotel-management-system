package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.RoomTypeRequest;
import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.entities.RoomType;
import com.nhom1.hotelmanagement.services.RoomTypeService;
import com.nhom1.hotelmanagement.services.RoomTypeImageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/roomtypes")
public class RoomTypeController {

    @Autowired
    private RoomTypeService roomTypeService;

    @Autowired
    private RoomTypeImageService roomTypeImageService;

    @GetMapping
    public String listRoomTypes(HttpSession session, Model model) {
        LoginResponse user = (LoginResponse) session.getAttribute("user");
        model.addAttribute("roomtypes", roomTypeService.listAllDto());
        if (user != null) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("isAdmin", "ADMIN".equals(user.getRole().toString()));
        }
        return "roomtypes";
    }

    @GetMapping("/create")
    public String createRoomTypeForm(Model model) {
        RoomTypeRequest roomTypeRequest = new RoomTypeRequest();
        roomTypeRequest.setImages(new java.util.ArrayList<>());
        model.addAttribute("roomtype", roomTypeRequest);
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
        model.addAttribute("roomTypeImages", roomTypeImageService.listByRoomTypeIdAsDto(id));
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

    @GetMapping("/{id}/images")
    public String viewRoomTypeImages(@PathVariable Long id, Model model) {
        RoomType roomType = roomTypeService.getById(id);
        if (roomType == null) {
            return "redirect:/roomtypes";
        }
        model.addAttribute("roomType", roomType);
        model.addAttribute("roomTypeImages", roomTypeImageService.listByRoomTypeId(id));
        return "roomtypeimages";
    }
}