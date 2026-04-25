package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.BookingDTO;
import com.nhom1.hotelmanagement.dto.BookingDetailDTO;
import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.entities.User;
import com.nhom1.hotelmanagement.services.BookingService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    public String showAllRoom(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            Model model) {
        LocalDate now = LocalDate.now();
        int selectedMonth = (month != null && month >= 1 && month <= 12) ? month : now.getMonthValue();
        int selectedYear = (year != null && year >= 2000 && year <= 2100) ? year : now.getYear();

        model.addAttribute("activePage", "bookstat");
        List<BookingDetailDTO> bookingList = bookingService.getAllBooking().stream()
                .filter(booking -> hasDetailInMonth(booking, selectedMonth, selectedYear))
                .toList();
        model.addAttribute("bookingList", bookingList);
        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("currentYear", now.getYear());
        return "bookstat";
    }

    private boolean hasDetailInMonth(BookingDetailDTO booking, int month, int year) {
        if (booking == null || booking.getDetails() == null) {
            return false;
        }

        return booking.getDetails().stream().anyMatch(detail ->
                isInMonthYear(detail.getCheckIn(), month, year)
                        || isInMonthYear(detail.getCheckOut(), month, year));
    }

    private boolean isInMonthYear(String dateTimeValue, int month, int year) {
        if (dateTimeValue == null || dateTimeValue.isBlank()) {
            return false;
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeValue);
            return dateTime.getMonthValue() == month && dateTime.getYear() == year;
        } catch (DateTimeParseException ex) {
            return false;
        }
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