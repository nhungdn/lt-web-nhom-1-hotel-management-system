package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.PaymentRequest;
import com.nhom1.hotelmanagement.entities.Booking;
import com.nhom1.hotelmanagement.entities.Payment;
import com.nhom1.hotelmanagement.services.PaymentService;
import com.nhom1.hotelmanagement.repositories.BookingRepository;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/payments")
    public String listPayments(Model model) {
        model.addAttribute("activePage", "payments");
        model.addAttribute("payments", paymentService.listAll());
        return "payments";
    }

    @GetMapping("/payments/create")
    public String createPaymentForm(Model model) {
        model.addAttribute("payment", new PaymentRequest());
        model.addAttribute("bookings", bookingRepository.findAll());
        return "payment-form";
    }

    @PostMapping("/payments/create")
    public String createPayment(@ModelAttribute PaymentRequest request) {
        paymentService.create(request);
        return "redirect:/payments";
    }

    @GetMapping("/payments/edit/{id}")
    public String editPaymentForm(@PathVariable Long id, Model model) {
        Payment payment = paymentService.getById(id);
        if (payment == null) {
            return "redirect:/payments";
        }

        PaymentRequest request = new PaymentRequest();
        request.setPaymentId(payment.getPaymentId());
        request.setTotalAmount(payment.getTotalAmount());
        request.setStatus(payment.getStatus());
        request.setBookingId(payment.getBooking() != null ? payment.getBooking().getBookingId() : null);
        request.setPaymentDate(payment.getPaymentDate() != null ? payment.getPaymentDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");

        model.addAttribute("payment", request);
        model.addAttribute("bookings", bookingRepository.findAll());
        return "payment-form";
    }

    @PostMapping("/payments/update/{id}")
    public String updatePayment(@PathVariable Long id, @ModelAttribute PaymentRequest request) {
        paymentService.update(id, request);
        return "redirect:/payments";
    }

    @PostMapping("/payments/delete/{id}")
    public String deletePayment(@PathVariable Long id) {
        paymentService.delete(id);
        return "redirect:/payments";
    }
}
