package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.BookingInvoiceDTO;
import com.nhom1.hotelmanagement.dto.PaymentRequest;
import com.nhom1.hotelmanagement.entities.Payment;
import com.nhom1.hotelmanagement.services.PaymentService;
import com.nhom1.hotelmanagement.repositories.BookingDetailRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PaymentController {
    private static final String PAYMENT_FORM_VIEW = "payment-form";

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @GetMapping("/payments")
    public String listPayments(
        @RequestParam(value = "month", required = false) Integer month,
        @RequestParam(value = "year", required = false) Integer year,
        Model model) {
        LocalDate now = LocalDate.now();
        int selectedMonth = (month != null && month >= 1 && month <= 12) ? month : now.getMonthValue();
        int selectedYear = (year != null && year >= 2000 && year <= 2100) ? year : now.getYear();

        model.addAttribute("activePage", "payments");
        model.addAttribute("payments", paymentService.listByMonthYear(selectedMonth, selectedYear));
        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("currentYear", now.getYear());
        return "payments";
    }

    @GetMapping("/payments/create")
    public String createPaymentForm(Model model) {
        model.addAttribute("payment", new PaymentRequest());
        model.addAttribute("bookingDetails", bookingDetailRepository.findAll());
        model.addAttribute("viewOnly", false);
        return PAYMENT_FORM_VIEW;
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

        model.addAttribute("payment", mapToPaymentRequest(payment));
        model.addAttribute("bookingDetails", bookingDetailRepository.findAll());
        model.addAttribute("viewOnly", false);
        return PAYMENT_FORM_VIEW;
    }

    @GetMapping("/payments/view/{id}")
    public String viewPaymentDetail(@PathVariable Long id, Model model) {
        Payment payment = paymentService.getById(id);
        if (payment == null) {
            return "redirect:/payments";
        }

        model.addAttribute("payment", mapToPaymentRequest(payment));
        model.addAttribute("bookingDetails", bookingDetailRepository.findAll());
        model.addAttribute("viewOnly", true);

        BookingInvoiceDTO invoiceSummary = null;
        BookingInvoiceDTO.InvoiceItemDTO selectedInvoice = null;
        if (payment.getBookingDetail() != null && payment.getBookingDetail().getBookingDetailId() != null) {
            invoiceSummary = paymentService.getDetailInvoiceSummary(payment.getBookingDetail().getBookingDetailId());
            selectedInvoice = invoiceSummary.getPaidInvoices().stream()
                .filter(invoice -> Objects.equals(invoice.getPaymentId(), payment.getPaymentId()))
                .findFirst()
                .orElse(null);
        }

        model.addAttribute("invoiceSummary", invoiceSummary);
        model.addAttribute("selectedInvoice", selectedInvoice);
        model.addAttribute("currentPaymentId", payment.getPaymentId());
        return PAYMENT_FORM_VIEW;
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

    @GetMapping("/payments/booking/{bookingId}/summary")
    @ResponseBody
    public ResponseEntity<BookingInvoiceDTO> getBookingInvoiceSummary(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getBookingInvoiceSummary(bookingId));
    }

    @GetMapping("/payments/detail/{detailId}/summary")
    @ResponseBody
    public ResponseEntity<BookingInvoiceDTO> getDetailInvoiceSummary(@PathVariable Long detailId) {
        return ResponseEntity.ok(paymentService.getDetailInvoiceSummary(detailId));
    }

    @PostMapping("/payments/pay/{bookingId}")
    public ResponseEntity<byte[]> payBooking(@PathVariable Long bookingId) {
        byte[] txtData = paymentService.payBookingAndGenerateInvoiceTxt(bookingId);
        return buildTxtDownloadResponse("invoice-booking-" + bookingId + ".txt", txtData);
    }

    @PostMapping("/payments/pay-detail/{detailId}")
    public ResponseEntity<byte[]> payDetail(@PathVariable Long detailId) {
        byte[] txtData = paymentService.payDetailAndGenerateInvoiceTxt(detailId);
        return buildTxtDownloadResponse("invoice-detail-" + detailId + ".txt", txtData);
        }

        private PaymentRequest mapToPaymentRequest(Payment payment) {
        PaymentRequest request = new PaymentRequest();
        request.setPaymentId(payment.getPaymentId());
        request.setTotalAmount(payment.getTotalAmount());
        request.setStatus(payment.getStatus());
        request.setBookingDetailId(
            payment.getBookingDetail() != null ? payment.getBookingDetail().getBookingDetailId() : null);
        request.setPaymentDate(
            payment.getPaymentDate() != null ? payment.getPaymentDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : "");
        return request;
        }

        private ResponseEntity<byte[]> buildTxtDownloadResponse(String fileName, byte[] txtData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDisposition(ContentDisposition.attachment()
            .filename(fileName)
                .build());
        return ResponseEntity.ok().headers(headers).body(txtData);
    }
}
