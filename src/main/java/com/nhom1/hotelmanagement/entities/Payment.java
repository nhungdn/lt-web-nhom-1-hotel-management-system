package com.nhom1.hotelmanagement.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_detail_id")
    private BookingDetail bookingDetail;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "status")
    private String status;  // PAID | UNPAID

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public BookingDetail getBookingDetail() { return bookingDetail; }
    public void setBookingDetail(BookingDetail bookingDetail) { this.bookingDetail = bookingDetail; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
