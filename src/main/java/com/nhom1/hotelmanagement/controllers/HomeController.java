
package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.BookingDTO;
import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.dto.RoomTypeResponse;
import com.nhom1.hotelmanagement.services.BookingService;
import com.nhom1.hotelmanagement.services.RoomTypeService;
import com.nhom1.hotelmanagement.services.RoomTypeImageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
    
    @Autowired private RoomTypeService roomTypeService; 
    @Autowired private RoomTypeImageService roomTypeImageService;
    @Autowired private BookingService bookingService;
    
    @GetMapping("/")
    public String showHome(HttpSession session, Model model) {
        // Pass room types and their images to homepage
        var roomTypes = roomTypeService.listAllDto();
        model.addAttribute("roomTypes", roomTypes);
        
        // Load images for each room type
        Map<Long, Object> roomTypeImages = new HashMap<>();
        roomTypes.forEach(rt -> {
            roomTypeImages.put(rt.getRoomTypeId(), roomTypeImageService.listByRoomTypeId(rt.getRoomTypeId()));
        });
        model.addAttribute("roomTypeImages", roomTypeImages);
        
        // Check if user is logged in
        LoginResponse user = (LoginResponse) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("isLoggedIn", true);
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        
        return "index";
    }
    
    @PostMapping("/filter")
    @ResponseBody
    public ResponseEntity<?> filterRoom(@RequestBody BookingDTO.FilterDate request){
        try{
            String start = request.getCheckIn();
            String end = request.getCheckOut();
            List<RoomTypeResponse> roomTypes = roomTypeService.filterAvailableRooms(start, end);
            
            Map<String, Object> response = new HashMap<>();
            response.put("roomTypes", roomTypes);

            Map<Long, Object> roomTypeImages = new HashMap<>();
            roomTypes.forEach(rt -> {
                roomTypeImages.put(rt.getRoomTypeId(), roomTypeImageService.listByRoomTypeId(rt.getRoomTypeId()));
            });
            response.put("roomTypeImages", roomTypeImages);

            return ResponseEntity.ok(response);
        } catch (Exception e){
            return ResponseEntity.status(500).body("Lỗi lọc phòng: " + e.getMessage());
        }
    }
    
    @PostMapping("/book")
    @ResponseBody
    public ResponseEntity<?> createBook(@RequestBody BookingDTO.MultiSubmitRequest request, HttpSession session){
        System.out.println("controllers: "+ request);
        List<String> errorRooms = bookingService.createBooking(request, session);
        if(!errorRooms.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(errorRooms);
        
        return ResponseEntity.ok("Đặt phòng thành công!");
    }
}

