package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.RoomTypeImageRequest;
import com.nhom1.hotelmanagement.services.RoomTypeImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Controller
@RequestMapping("/roomtypeimages")
public class RoomTypeImageController {
    
    @Autowired
    private RoomTypeImageService roomTypeImageService;
    
    @GetMapping
    public String listAllRoomTypeImages(Model model) {
        // This endpoint is optional - not used in the current flow
        return "roomtypeimages";
    }
    
    @GetMapping("/create/{roomTypeId}")
    public String createRoomTypeImageForm(@PathVariable Long roomTypeId, Model model) {
        model.addAttribute("roomTypeId", roomTypeId);
        model.addAttribute("image", new RoomTypeImageRequest());
        model.addAttribute("returnUrl", "/roomtypes/" + roomTypeId + "/images");
        return "roomtypeimage-form";
    }
    
    @PostMapping("/create/{roomTypeId}")
    public String createRoomTypeImage(@PathVariable Long roomTypeId, @ModelAttribute RoomTypeImageRequest dto,
                                      @RequestParam(value = "returnUrl", required = false) String returnUrl) {
        roomTypeImageService.create(roomTypeId, dto);
        
        if (returnUrl != null && !returnUrl.isEmpty()) {
            return "redirect:" + returnUrl;
        }
        return "redirect:/roomtypeimages";
    }
    
    @GetMapping("/edit/{id}")
    public String editRoomTypeImageForm(@PathVariable Long id, 
                                        @RequestParam(value = "returnUrl", required = false) String returnUrl,
                                        Model model) {
        RoomTypeImageRequest dto = roomTypeImageService.toDto(roomTypeImageService.getById(id));
        if (dto == null) {
            return "redirect:/";
        }
        
        model.addAttribute("image", dto);
        model.addAttribute("imageId", id);
        model.addAttribute("returnUrl", returnUrl != null ? returnUrl : "/");
        return "roomtypeimage-form";
    }
    
    @PostMapping("/update/{id}")
    public String updateRoomTypeImage(@PathVariable Long id, @ModelAttribute RoomTypeImageRequest dto,
                                      @RequestParam(value = "returnUrl", required = false) String returnUrl) {
        roomTypeImageService.update(id, dto);
        
        if (returnUrl != null && !returnUrl.isEmpty()) {
            return "redirect:" + returnUrl;
        }
        return "redirect:/roomtypeimages";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteRoomTypeImage(@PathVariable Long id,
                                      @RequestParam(value = "returnUrl", required = false) String returnUrl) {
        roomTypeImageService.delete(id);
        
        if (returnUrl != null && !returnUrl.isEmpty()) {
            return "redirect:" + returnUrl;
        }
        return "redirect:/roomtypeimages";
    }

    @GetMapping("/api/by-roomtype/{roomTypeId}")
    @ResponseBody
    public ResponseEntity<List<RoomTypeImageRequest>> getImagesByRoomType(@PathVariable Long roomTypeId) {
        List<RoomTypeImageRequest> images = roomTypeImageService.listByRoomTypeIdAsDto(roomTypeId);
        return ResponseEntity.ok(images);
    }
}
