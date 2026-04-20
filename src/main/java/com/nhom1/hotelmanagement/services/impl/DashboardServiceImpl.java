package com.nhom1.hotelmanagement.services.impl;

import com.nhom1.hotelmanagement.dto.BookingDetailDashboardDTO;
import com.nhom1.hotelmanagement.entities.BookingDetail;
import com.nhom1.hotelmanagement.entities.Payment;
import com.nhom1.hotelmanagement.entities.Room;
import com.nhom1.hotelmanagement.repositories.BookingDetailRepository;
import com.nhom1.hotelmanagement.repositories.PaymentRepository;
import com.nhom1.hotelmanagement.repositories.RoomRepository;
import com.nhom1.hotelmanagement.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public long getTotalBookings() {
        return bookingDetailRepository.count();
    }

    @Override
    public BigDecimal getTotalRevenue() {
        BigDecimal total = paymentRepository.sumByStatus("PAID");
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public long countRoomByStatus(String status) {
        return roomRepository.countByStatus(Room.Status.valueOf(status));
    }

    @Override
    public long countBookingByStatus(String status) {
        return bookingDetailRepository.countByStatus(status);
    }

    @Override
    public long countUnpaidPayments() {
        return paymentRepository.countByStatus("UNPAID");
    }

    @Override
    public List<BookingDetailDashboardDTO> getRecentBookings(int limit) {
        List<BookingDetail> list = bookingDetailRepository.findRecentBookings(
                PageRequest.of(0, limit)
        );
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<Payment> getRecentPayments(int limit) {
        try {
            List<Payment> payments = paymentRepository.findRecentPayments(PageRequest.of(0, limit));
            return payments != null ? payments : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private BookingDetailDashboardDTO toDto(BookingDetail bd) {
        BookingDetailDashboardDTO dto = new BookingDetailDashboardDTO();
        dto.setBookingDetailId(bd.getBookingDetailId());
        dto.setStatus(bd.getStatus());
        dto.setCheckInDate(bd.getCheckInDate());
        dto.setCheckOutDate(bd.getCheckOutDate());

        // ✅ FIX: BookingDetail.priceAtBooking là Double, DTO nhận BigDecimal → phải convert
        if (bd.getPriceAtBooking() != null) {
            dto.setPriceAtBooking(BigDecimal.valueOf(bd.getPriceAtBooking()));
        }

        if (bd.getBooking() != null && bd.getBooking().getCustomer() != null) {
            dto.setCustomerName(bd.getBooking().getCustomer().getName());
            dto.setCustomerPhone(bd.getBooking().getCustomer().getPhone());
        }

        if (bd.getRoom() != null) {
            dto.setRoomNumber(bd.getRoom().getRoomNumber());
            if (bd.getRoom().getRoomType() != null) {
                dto.setRoomTypeName(bd.getRoom().getRoomType().getName());
            }
        }

        return dto;
    }
}