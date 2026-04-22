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
import java.util.LinkedHashMap;
import java.util.Map;
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
        return bookingDetailRepository.countByStatus(BookingDetail.Status.valueOf(status));
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
    public Map<Integer, BigDecimal> getRevenueByDay(int year, int month) {
        List<Object[]> rows = paymentRepository.revenueByDay(year, month);
        Map<Integer, BigDecimal> result = new LinkedHashMap<>();
        // Điền đủ 28-31 ngày, ngày không có doanh thu = 0
        int daysInMonth = java.time.YearMonth.of(year, month).lengthOfMonth();
        for (int d = 1; d <= daysInMonth; d++) result.put(d, BigDecimal.ZERO);
        for (Object[] row : rows) {
            result.put(((Number) row[0]).intValue(), (BigDecimal) row[1]);
        }
        return result;
    }

    @Override
    public Map<Integer, BigDecimal> getRevenueByMonth(int year) {
        List<Object[]> rows = paymentRepository.revenueByMonth(year);
        Map<Integer, BigDecimal> result = new LinkedHashMap<>();
        // Điền đủ 12 tháng, tháng không có = 0
        for (int m = 1; m <= 12; m++) result.put(m, BigDecimal.ZERO);
        for (Object[] row : rows) {
            result.put(((Number) row[0]).intValue(), (BigDecimal) row[1]);
        }
        return result;
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

        if (bd.getPriceAtBooking() != null) {
            dto.setPriceAtBooking(bd.getPriceAtBooking());
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