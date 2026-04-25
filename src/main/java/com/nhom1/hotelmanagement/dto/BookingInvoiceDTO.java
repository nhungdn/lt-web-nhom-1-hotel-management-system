package com.nhom1.hotelmanagement.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class BookingInvoiceDTO {
    private Long bookingId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private BigDecimal paidTotal = BigDecimal.ZERO;
    private BigDecimal unpaidTotal = BigDecimal.ZERO;
    private BigDecimal grandTotal = BigDecimal.ZERO;
    private List<InvoiceItemDTO> paidInvoices = new ArrayList<>();
    private List<InvoiceItemDTO> unpaidInvoices = new ArrayList<>();

    @Data
    public static class InvoiceItemDTO {
        private Long paymentId;
        private Long bookingDetailId;
        private String roomNumber;
        private String status;
        private String paymentDate;
        private BigDecimal amount = BigDecimal.ZERO;
        private BigDecimal originalTotal = BigDecimal.ZERO;
        private BigDecimal alreadyPaid = BigDecimal.ZERO;
        private BigDecimal remaining = BigDecimal.ZERO;
        private List<ChargeLineDTO> chargeLines = new ArrayList<>();
    }

    @Data
    public static class ChargeLineDTO {
        private String label;
        private Integer quantity;
        private BigDecimal unitPrice = BigDecimal.ZERO;
        private BigDecimal lineTotal = BigDecimal.ZERO;
    }
}
