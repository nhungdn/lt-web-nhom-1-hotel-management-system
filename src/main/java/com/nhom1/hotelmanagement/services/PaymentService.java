package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.BookingInvoiceDTO;
import com.nhom1.hotelmanagement.dto.PaymentRequest;
import com.nhom1.hotelmanagement.entities.Booking;
import com.nhom1.hotelmanagement.entities.BookingDetail;
import com.nhom1.hotelmanagement.entities.BookingHotelService;
import com.nhom1.hotelmanagement.entities.Payment;
import com.nhom1.hotelmanagement.repositories.BookingRepository;
import com.nhom1.hotelmanagement.repositories.BookingDetailRepository;
import com.nhom1.hotelmanagement.repositories.PaymentRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_UNPAID = "UNPAID";
    private static final String DEFAULT_ROOM_NUMBER = "N/A";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static class ChargeComponent {
        private final String label;
        private final Integer quantity;
        private final BigDecimal unitPrice;
        private BigDecimal remaining;

        private ChargeComponent(String label, Integer quantity, BigDecimal unitPrice, BigDecimal remaining) {
            this.label = label;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.remaining = remaining;
        }
    }

    public List<Payment> listAll() {
        return paymentRepository.findAll();
    }

    public List<Payment> listByMonthYear(int month, int year) {
        return paymentRepository.findByPaymentMonthYear(month, year);
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

    @Transactional(readOnly = true)
    public BookingInvoiceDTO getBookingInvoiceSummary(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking không tồn tại: " + bookingId));

        List<BookingDetail> details = booking.getBookingDetails();
        List<Payment> bookingPayments = paymentRepository.findByBookingDetail_Booking_BookingId(bookingId);

        Map<Long, List<Payment>> paymentsByDetail = new HashMap<>();
        for (Payment payment : bookingPayments) {
            if (payment.getBookingDetail() == null || payment.getBookingDetail().getBookingDetailId() == null) {
                continue;
            }
            Long detailId = payment.getBookingDetail().getBookingDetailId();
            paymentsByDetail.computeIfAbsent(detailId, key -> new ArrayList<>()).add(payment);
        }

        BookingInvoiceDTO summary = new BookingInvoiceDTO();
        summary.setBookingId(booking.getBookingId());
        populateCustomerInfo(summary, booking);

        BigDecimal paidTotal = BigDecimal.ZERO;
        BigDecimal unpaidTotal = BigDecimal.ZERO;

        for (BookingDetail detail : details) {
            BigDecimal detailTotal = calculateDetailTotal(detail);
            BigDecimal detailPaid = BigDecimal.ZERO;
            List<ChargeComponent> components = initializeChargeComponents(detail);

            List<Payment> detailPayments = sortPayments(
                    paymentsByDetail.getOrDefault(detail.getBookingDetailId(), List.of()));
            for (Payment payment : detailPayments) {
                if (!isPaid(payment)) {
                    continue;
                }

                BigDecimal paymentAmount = nonNull(payment.getTotalAmount());
                detailPaid = detailPaid.add(paymentAmount);

                summary.getPaidInvoices()
                        .add(createPaidInvoiceItem(detail, payment, detailTotal, detailPaid, components));
                paidTotal = paidTotal.add(paymentAmount);
            }

            BigDecimal remaining = maxZero(detailTotal.subtract(detailPaid));
            if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                BookingInvoiceDTO.InvoiceItemDTO unpaidItem = new BookingInvoiceDTO.InvoiceItemDTO();
                unpaidItem.setBookingDetailId(detail.getBookingDetailId());
                unpaidItem.setRoomNumber(resolveRoomNumber(detail));
                unpaidItem.setStatus(STATUS_UNPAID);
                unpaidItem.setAmount(remaining);
                unpaidItem.setOriginalTotal(detailTotal);
                unpaidItem.setAlreadyPaid(detailPaid);
                unpaidItem.setRemaining(remaining);
                unpaidItem.setChargeLines(remainingChargeLines(components));

                summary.getUnpaidInvoices().add(unpaidItem);
                unpaidTotal = unpaidTotal.add(remaining);
            }
        }

        summary.getPaidInvoices().sort(Comparator.comparing(BookingInvoiceDTO.InvoiceItemDTO::getPaymentDate,
                Comparator.nullsLast(String::compareTo)).reversed());
        summary.getUnpaidInvoices().sort(Comparator.comparing(BookingInvoiceDTO.InvoiceItemDTO::getBookingDetailId));

        summary.setPaidTotal(paidTotal);
        summary.setUnpaidTotal(unpaidTotal);
        summary.setGrandTotal(paidTotal.add(unpaidTotal));
        return summary;
    }

    @Transactional
    public byte[] payBookingAndGenerateInvoiceTxt(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking không tồn tại: " + bookingId));

        for (BookingDetail detail : booking.getBookingDetails()) {
            payRemainingForDetail(detail);
        }

        BookingInvoiceDTO summary = getBookingInvoiceSummary(bookingId);
        return buildInvoiceTxt(summary).getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(readOnly = true)
    public BookingInvoiceDTO getDetailInvoiceSummary(Long detailId) {
        BookingDetail detail = bookingDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("Chi tiet dat phong khong ton tai: " + detailId));
        return buildDetailSummary(detail);
    }

    @Transactional
    public byte[] payDetailAndGenerateInvoiceTxt(Long detailId) {
        BookingDetail detail = bookingDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("Chi tiet dat phong khong ton tai: " + detailId));

        payRemainingForDetail(detail);

        BookingInvoiceDTO summary = buildDetailSummary(detail);
        return buildInvoiceTxt(summary).getBytes(StandardCharsets.UTF_8);
    }

    private void updateFromRequest(Payment payment, PaymentRequest request) {
        if (request.getTotalAmount() != null) {
            payment.setTotalAmount(request.getTotalAmount());
        }
        payment.setStatus(
            request.getStatus() == null || request.getStatus().isBlank() ? STATUS_PENDING : request.getStatus());
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

    private BigDecimal calculateDetailTotal(BookingDetail detail) {
        BigDecimal roomPrice = nonNull(detail.getPriceAtBooking());
        BigDecimal servicePrice = detail.getBookingHotelServices().stream()
                .map(this::calculateServiceLine)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return roomPrice.add(servicePrice);
    }

    private BigDecimal calculateServiceLine(BookingHotelService bhs) {
        if (bhs.getService() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal unitPrice = nonNull(bhs.getService().getPrice());
        BigDecimal quantity = BigDecimal.valueOf(bhs.getQuantity() == null ? 0 : bhs.getQuantity());
        return unitPrice.multiply(quantity);
    }

    private List<ChargeComponent> initializeChargeComponents(BookingDetail detail) {
        List<ChargeComponent> components = new ArrayList<>();
        components.add(new ChargeComponent(
                "Tien phong - P" + (detail.getRoom() != null ? detail.getRoom().getRoomNumber() : "N/A"),
                1,
                nonNull(detail.getPriceAtBooking()),
                nonNull(detail.getPriceAtBooking())));

        for (BookingHotelService bhs : detail.getBookingHotelServices()) {
            if (bhs.getService() == null) {
                continue;
            }
            components.add(new ChargeComponent(
                    "Dich vu - " + bhs.getService().getName(),
                    bhs.getQuantity() == null ? 0 : bhs.getQuantity(),
                    nonNull(bhs.getService().getPrice()),
                    calculateServiceLine(bhs)));
        }
        return components;
    }

    private List<Payment> sortPayments(List<Payment> payments) {
        List<Payment> sorted = new ArrayList<>(payments);
        sorted.sort(Comparator
                .comparing(Payment::getPaymentDate, Comparator.nullsLast(LocalDateTime::compareTo))
                .thenComparing(Payment::getPaymentId, Comparator.nullsLast(Long::compareTo)));
        return sorted;
    }

    private List<BookingInvoiceDTO.ChargeLineDTO> allocateChargeLines(List<ChargeComponent> components,
            BigDecimal amount) {
        List<BookingInvoiceDTO.ChargeLineDTO> lines = new ArrayList<>();
        BigDecimal remainingToAllocate = nonNull(amount);

        for (ChargeComponent component : components) {
            if (remainingToAllocate.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            if (component.remaining.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal allocated = component.remaining.min(remainingToAllocate);
            if (allocated.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            component.remaining = component.remaining.subtract(allocated);
            remainingToAllocate = remainingToAllocate.subtract(allocated);

            BookingInvoiceDTO.ChargeLineDTO line = new BookingInvoiceDTO.ChargeLineDTO();
            line.setLabel(component.label);
            line.setQuantity(component.quantity);
            line.setUnitPrice(component.unitPrice);
            line.setLineTotal(allocated);
            lines.add(line);
        }

        return lines;
    }

    private List<BookingInvoiceDTO.ChargeLineDTO> remainingChargeLines(List<ChargeComponent> components) {
        List<BookingInvoiceDTO.ChargeLineDTO> lines = new ArrayList<>();
        for (ChargeComponent component : components) {
            if (component.remaining.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BookingInvoiceDTO.ChargeLineDTO line = new BookingInvoiceDTO.ChargeLineDTO();
            line.setLabel(component.label);
            line.setQuantity(component.quantity);
            line.setUnitPrice(component.unitPrice);
            line.setLineTotal(component.remaining);
            lines.add(line);
        }
        return lines;
    }

    private void populateCustomerInfo(BookingInvoiceDTO summary, Booking booking) {
        if (booking.getCustomer() == null) {
            return;
        }
        summary.setCustomerName(booking.getCustomer().getName());
        summary.setCustomerPhone(booking.getCustomer().getPhone());
        summary.setCustomerEmail(booking.getCustomer().getEmail());
    }

    private String resolveRoomNumber(BookingDetail detail) {
        return detail.getRoom() != null ? detail.getRoom().getRoomNumber() : DEFAULT_ROOM_NUMBER;
    }

    private BookingInvoiceDTO.InvoiceItemDTO createPaidInvoiceItem(
            BookingDetail detail,
            Payment payment,
            BigDecimal detailTotal,
            BigDecimal detailPaid,
            List<ChargeComponent> components) {
        BigDecimal paymentAmount = nonNull(payment.getTotalAmount());

        BookingInvoiceDTO.InvoiceItemDTO paidItem = new BookingInvoiceDTO.InvoiceItemDTO();
        paidItem.setPaymentId(payment.getPaymentId());
        paidItem.setBookingDetailId(detail.getBookingDetailId());
        paidItem.setRoomNumber(resolveRoomNumber(detail));
        paidItem.setStatus(STATUS_PAID);
        paidItem.setPaymentDate(formatDate(payment.getPaymentDate()));
        paidItem.setAmount(paymentAmount);
        paidItem.setOriginalTotal(detailTotal);
        paidItem.setAlreadyPaid(detailPaid);
        paidItem.setRemaining(maxZero(detailTotal.subtract(detailPaid)));
        paidItem.setChargeLines(allocateChargeLines(components, paymentAmount));
        return paidItem;
    }

    private BigDecimal calculatePaidForDetail(Long detailId) {
        return paymentRepository.findByBookingDetail_BookingDetailId(detailId).stream()
                .filter(this::isPaid)
                .map(Payment::getTotalAmount)
                .map(this::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void payRemainingForDetail(BookingDetail detail) {
        BigDecimal total = calculateDetailTotal(detail);
        BigDecimal paid = calculatePaidForDetail(detail.getBookingDetailId());
        BigDecimal remaining = maxZero(total.subtract(paid));

        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        Payment payment = new Payment();
        payment.setBookingDetail(detail);
        payment.setTotalAmount(remaining);
        payment.setStatus(STATUS_PAID);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    private boolean isPaid(Payment payment) {
        return payment.getStatus() != null && STATUS_PAID.equalsIgnoreCase(payment.getStatus().trim());
    }

    private BigDecimal maxZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : value;
    }

    private BigDecimal nonNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String formatDate(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.format(DATE_TIME_FORMATTER);
    }

    private String formatMoney(BigDecimal amount) {
        return String.format(Locale.US, "%,.0f", nonNull(amount));
    }

    private String buildInvoiceTxt(BookingInvoiceDTO summary) {
        StringBuilder sb = new StringBuilder();
        sb.append("HOTEL INVOICE").append("\n");
        sb.append("Booking ID: #").append(summary.getBookingId()).append("\n");
        sb.append("Khach hang: ").append(summary.getCustomerName() == null ? "N/A" : summary.getCustomerName())
                .append("\n");
        sb.append("So dien thoai: ").append(summary.getCustomerPhone() == null ? "N/A" : summary.getCustomerPhone())
                .append("\n");
        sb.append("Email: ").append(summary.getCustomerEmail() == null ? "N/A" : summary.getCustomerEmail())
                .append("\n");
        sb.append("Ngay xuat: ").append(formatDate(LocalDateTime.now())).append("\n\n");

        sb.append("=== HOA DON DA THANH TOAN ===\n");
        if (summary.getPaidInvoices().isEmpty()) {
            sb.append("(Khong co)\n");
        } else {
            for (BookingInvoiceDTO.InvoiceItemDTO item : summary.getPaidInvoices()) {
                sb.append("- Payment #").append(item.getPaymentId() == null ? "N/A" : item.getPaymentId())
                        .append(" | Detail #").append(item.getBookingDetailId())
                        .append(" | Phong ").append(item.getRoomNumber())
                        .append(" | ").append(formatMoney(item.getAmount())).append(" VND")
                        .append(" | ").append(item.getPaymentDate() == null ? "N/A" : item.getPaymentDate())
                        .append("\n");
                for (BookingInvoiceDTO.ChargeLineDTO line : item.getChargeLines()) {
                    sb.append("  + ").append(line.getLabel())
                            .append(" x").append(line.getQuantity())
                            .append(" : ").append(formatMoney(line.getLineTotal())).append(" VND\n");
                }
            }
        }

        sb.append("\n=== HOA DON CHUA THANH TOAN ===\n");
        if (summary.getUnpaidInvoices().isEmpty()) {
            sb.append("(Khong con cong no)\n");
        } else {
            for (BookingInvoiceDTO.InvoiceItemDTO item : summary.getUnpaidInvoices()) {
                sb.append("- Detail #").append(item.getBookingDetailId())
                        .append(" | Phong ").append(item.getRoomNumber())
                        .append(" | Con lai: ").append(formatMoney(item.getAmount())).append(" VND\n");
                for (BookingInvoiceDTO.ChargeLineDTO line : item.getChargeLines()) {
                    sb.append("  + ").append(line.getLabel())
                            .append(" x").append(line.getQuantity())
                            .append(" : ").append(formatMoney(line.getLineTotal())).append(" VND\n");
                }
            }
        }

        sb.append("\nTong da thanh toan: ").append(formatMoney(summary.getPaidTotal())).append(" VND\n");
        sb.append("Tong chua thanh toan: ").append(formatMoney(summary.getUnpaidTotal())).append(" VND\n");
        sb.append("Tong booking: ").append(formatMoney(summary.getGrandTotal())).append(" VND\n");
        return sb.toString();
    }

    private BookingInvoiceDTO buildDetailSummary(BookingDetail detail) {
        BookingInvoiceDTO summary = new BookingInvoiceDTO();
        if (detail.getBooking() != null) {
            summary.setBookingId(detail.getBooking().getBookingId());
            populateCustomerInfo(summary, detail.getBooking());
        }

        BigDecimal detailTotal = calculateDetailTotal(detail);
        BigDecimal paidTotal = BigDecimal.ZERO;
        List<ChargeComponent> components = initializeChargeComponents(detail);

        List<Payment> payments = sortPayments(
                paymentRepository.findByBookingDetail_BookingDetailId(detail.getBookingDetailId()));
        for (Payment payment : payments) {
            if (!isPaid(payment)) {
                continue;
            }

            BigDecimal paymentAmount = nonNull(payment.getTotalAmount());
            paidTotal = paidTotal.add(paymentAmount);
            summary.getPaidInvoices().add(createPaidInvoiceItem(detail, payment, detailTotal, paidTotal, components));
        }

        BigDecimal unpaid = maxZero(detailTotal.subtract(paidTotal));
        if (unpaid.compareTo(BigDecimal.ZERO) > 0) {
            BookingInvoiceDTO.InvoiceItemDTO unpaidItem = new BookingInvoiceDTO.InvoiceItemDTO();
            unpaidItem.setBookingDetailId(detail.getBookingDetailId());
            unpaidItem.setRoomNumber(resolveRoomNumber(detail));
            unpaidItem.setStatus(STATUS_UNPAID);
            unpaidItem.setAmount(unpaid);
            unpaidItem.setOriginalTotal(detailTotal);
            unpaidItem.setAlreadyPaid(paidTotal);
            unpaidItem.setRemaining(unpaid);
            unpaidItem.setChargeLines(remainingChargeLines(components));
            summary.getUnpaidInvoices().add(unpaidItem);
        }

        summary.setPaidTotal(paidTotal);
        summary.setUnpaidTotal(unpaid);
        summary.setGrandTotal(detailTotal);
        return summary;
    }
}
