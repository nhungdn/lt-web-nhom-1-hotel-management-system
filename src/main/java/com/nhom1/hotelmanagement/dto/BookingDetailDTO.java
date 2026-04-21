
package com.nhom1.hotelmanagement.dto;

import com.nhom1.hotelmanagement.entities.BookingDetail.Status;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class BookingDetailDTO {
    private Long bookingId; // cho BE
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerIDCard;
    private BigDecimal totalAmount;
    private List<DetailDTO> details;
    @Data
    public static class DetailDTO {
        private Long bookingDetailId; // Cho BE
        private String roomNumber;
        private Long roomId;          // cho BE
        private String checkIn;
        private String checkOut;
        private Status status;
        private BigDecimal price;
        private List<ServiceDTO> services;
    }
    
    @Data
    public static class ServiceDTO {
        private Long hotelServiceId; // Cho BE
        private String serviceName;
        private Integer quantity;
        private BigDecimal price;
    }
}
