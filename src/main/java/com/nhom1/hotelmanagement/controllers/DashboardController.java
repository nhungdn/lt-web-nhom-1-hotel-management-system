package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.time.LocalDate;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private HotelServiceService hotelServiceService;

    @Autowired
    private UserService userService;

    @Autowired
    private DashboardExcelService dashboardExcelService;

    @GetMapping
    public String showDashboard(HttpSession session, Model model) {
        LoginResponse user = (LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Thêm vào showDashboard() — danh sách năm cho dropdown
        int currentYear = java.time.LocalDate.now().getYear();
        model.addAttribute("years",
                java.util.List.of(currentYear, currentYear - 1, currentYear - 2));

        model.addAttribute("user", user);
        model.addAttribute("activePage", "dashboard");

        // Stat cards
        model.addAttribute("totalBookings",   dashboardService.getTotalBookings());
        model.addAttribute("totalRevenue",    dashboardService.getTotalRevenue());
        model.addAttribute("availableRooms",  dashboardService.countRoomByStatus("AVAILABLE"));
        model.addAttribute("occupiedRooms",   dashboardService.countRoomByStatus("OCCUPIED"));
        model.addAttribute("cleaningRooms",   dashboardService.countRoomByStatus("CLEANING"));
        model.addAttribute("bookedRooms",     dashboardService.countRoomByStatus("BOOKED"));
        model.addAttribute("pendingBookings", dashboardService.countBookingByStatus("PENDING"));
        model.addAttribute("checkedIn",       dashboardService.countBookingByStatus("CHECKED_IN"));
        model.addAttribute("unpaidCount",     dashboardService.countUnpaidPayments());

        // Tables
        model.addAttribute("recentBookings", dashboardService.getRecentBookings(10));
        model.addAttribute("rooms",          roomService.getFullRoomList());
        model.addAttribute("payments",       dashboardService.getRecentPayments(5));
        model.addAttribute("services",       hotelServiceService.listAllDto());
        model.addAttribute("users",          userService.getAllUsers());

        return "dashboard/index";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel(HttpSession session) throws IOException {
        LoginResponse user = (LoginResponse) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(302)
                    .header(HttpHeaders.LOCATION, "/login")
                    .build();
        }

        byte[] data = dashboardExcelService.generateReport();
        String filename = "BaoCaoKhachSan_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmm"))
                + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    // API trả doanh thu theo ngày + tháng (gọi bằng JS fetch)
    @GetMapping("/revenue")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRevenue(
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month,
            HttpSession session) {

        if (session.getAttribute("user") == null)
            return ResponseEntity.status(401).build();

        int currentYear  = java.time.LocalDate.now().getYear();
        int currentMonth = java.time.LocalDate.now().getMonthValue();
        if (year  == 0) year  = currentYear;
        if (month == 0) month = currentMonth;

        Map<String, Object> data = new HashMap<>();
        data.put("revenueByDay",   dashboardService.getRevenueByDay(year, month));
        data.put("revenueByMonth", dashboardService.getRevenueByMonth(year));
        data.put("selectedYear",   year);
        data.put("selectedMonth",  month);

        return ResponseEntity.ok(data);
    }
}