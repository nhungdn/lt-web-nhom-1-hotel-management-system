package com.nhom1.hotelmanagement.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhom1.hotelmanagement.dto.BookingDTO;
import com.nhom1.hotelmanagement.dto.BookingDetailDTO;
import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.dto.RoomStatDTO;
import com.nhom1.hotelmanagement.entities.User;
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

    // ✅ THÊM: xử lý route /booking (trang danh sách booking)
    @GetMapping
    public String showBookingList(Model model) {
        model.addAttribute("activePage", "bookings");
        return "booking/index"; // templates/booking/index.html
    }

    @GetMapping("/status")
    public String showAllRoom(Model model) {
        model.addAttribute("activePage", "bookstat");
        List<BookingDetailDTO> bookingList = bookingService.getAllBooking();
        model.addAttribute("bookingList", bookingList);
        return "bookstat";
    }

    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity<?> editBook(@RequestBody BookingDetailDTO request) {
        List<String> error = bookingService.editBooking(request);
        if (!error.isEmpty())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        return ResponseEntity.ok("Sửa đơn thành công!");
    }

    @PostMapping("/cancel")
    @ResponseBody
    public String cancelBook(@RequestBody BookingDTO.CancelBook request, HttpSession session) {
        bookingService.cancelBooking(request.getId(), request.isDetail());
        LoginResponse user = (LoginResponse) session.getAttribute("user");
        if (user.getRole() == User.Role.ADMIN)
            return "roomstat";
        return "redirect:/";
    }
    
    @PostMapping("/change-status")
    @ResponseBody
    public ResponseEntity<String> changeStatus(@RequestBody BookingDTO.StatusDTO request, HttpSession session) {
        bookingService.changeStatus(request.getId(), request.getStatus());
        return ResponseEntity.ok("Sửa đơn thành công!");
    }
    
}