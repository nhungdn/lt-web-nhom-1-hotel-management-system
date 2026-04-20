package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.BookingDetailDashboardDTO;
import com.nhom1.hotelmanagement.entities.Payment;

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
}