
package com.nhom1.hotelmanagement.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhom1.hotelmanagement.dto.BookingDTO;
import com.nhom1.hotelmanagement.dto.BookingDetailDTO;
import com.nhom1.hotelmanagement.dto.RoomStatDTO;
import com.nhom1.hotelmanagement.entities.Room;
import com.nhom1.hotelmanagement.services.BookingService;
import com.nhom1.hotelmanagement.services.RoomService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/booking")
public class BookingController {
    @Autowired 
    private BookingService bookingService;
    @Autowired 
    private RoomService roomService;
    
    @GetMapping("/status")
    public String showAllRoom(Model model){
        List<RoomStatDTO> roomlist = roomService.getFullRoomList();
        try {
            ObjectMapper mapper = new ObjectMapper();
            String roomListJson = mapper.writeValueAsString(roomlist);
            model.addAttribute("roomList", roomListJson);

        } catch (JsonProcessingException e) {
            model.addAttribute("roomList", "[]");
        }
        return "bookstat";
    }
    
    @GetMapping("/status/{id}")
    public void showBookHistory(){
         
    }
    
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createBook(@RequestBody BookingDTO.MultiSubmitRequest request, HttpSession session){
        List<String> errorRooms = bookingService.checkRooms(request);
        if(!errorRooms.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(errorRooms);
        
       bookingService.createBooking(request, session);
        return ResponseEntity.ok("Đặt phòng thành công!");
    }
            
    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity<?> editBook(@RequestBody BookingDetailDTO request){
        List<String> error= bookingService.editBooking(request);
        if(!error.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        return ResponseEntity.ok("Sửa đơn thành công!");
    }
    
    @PostMapping("/cancel")
    public String cancelBook(){
        return "roomstat";
    }
    
    @PostMapping("/delete")
    public String deleteBook(){
        return "roomstat";
    }
    
}
