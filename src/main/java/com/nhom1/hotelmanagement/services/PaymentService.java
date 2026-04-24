package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.PaymentRequest;
import com.nhom1.hotelmanagement.entities.Payment;
import com.nhom1.hotelmanagement.repositories.BookingDetailRepository;
import com.nhom1.hotelmanagement.repositories.PaymentRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    public List<Payment> listAll() {
        return paymentRepository.findAll();
    }

    public Payment getById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public Payment create(PaymentRequest request) {
        Payment payment = new Payment();
        updateFromRequest(payment, request);
        return paymentRepository.save(payment);
    }

    public Payment update(Long id, PaymentRequest request) {
        Payment existing = paymentRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        updateFromRequest(existing, request);
        return paymentRepository.save(existing);
    }

    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }

    private void updateFromRequest(Payment payment, PaymentRequest request) {
        if (request.getTotalAmount() != null) {
            payment.setTotalAmount(request.getTotalAmount());
        }
        payment.setStatus(request.getStatus() == null || request.getStatus().isBlank() ? "PENDING" : request.getStatus());
        payment.setPaymentDate(parsePaymentDate(request.getPaymentDate()));

        if (request.getBookingDetailId() != null) {
            bookingDetailRepository.findById(request.getBookingDetailId()).ifPresent(payment::setBookingDetail);
        }
    }

    private LocalDateTime parsePaymentDate(String paymentDate) {
        if (paymentDate == null || paymentDate.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(paymentDate);
        } catch (Exception ex) {
            return LocalDateTime.now();
        }
    }
}
