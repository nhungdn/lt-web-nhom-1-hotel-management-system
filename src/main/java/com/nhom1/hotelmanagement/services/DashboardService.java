package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.BookingDetailDashboardDTO;
import com.nhom1.hotelmanagement.entities.Payment;
import java.util.Map;
import java.math.BigDecimal;
import java.util.List;

public interface DashboardService {

    long getTotalBookings();

    BigDecimal getTotalRevenue();

    long countRoomByStatus(String status);

    long countBookingByStatus(String status);

    long countUnpaidPayments();

    List<BookingDetailDashboardDTO> getRecentBookings(int limit);

    List<Payment> getRecentPayments(int limit);


    // Trả về Map<ngày, doanh thu> cho tháng được chọn
    Map<Integer, BigDecimal> getRevenueByDay(int year, int month);

    // Trả về Map<tháng, doanh thu> cho cả năm
    Map<Integer, BigDecimal> getRevenueByMonth(int year);
}