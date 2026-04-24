package com.nhom1.hotelmanagement.dto;

import java.math.BigDecimal;

public class PaymentRequest {
    private Long paymentId;
    private BigDecimal totalAmount;
    private String paymentDate;
    private String status;
    private Long bookingDetailId;

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getBookingDetailId() {
        return bookingDetailId;
    }

    public void setBookingDetailId(Long bookingDetailId) {
        this.bookingDetailId = bookingDetailId;
    }
}
