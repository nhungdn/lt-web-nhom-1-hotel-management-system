package com.nhom1.hotelmanagement.dto;

import com.nhom1.hotelmanagement.entities.BookingDetail.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BookingDetailDashboardDTO {
    private Long bookingDetailId;
    private String customerName;
    private String customerPhone;
    private String roomNumber;
    private String roomTypeName;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private Status status;
    private BigDecimal priceAtBooking;
    private BigDecimal    roomTypePrice;
}