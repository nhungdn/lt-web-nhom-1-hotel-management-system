
package com.nhom1.hotelmanagement.dto;

import java.util.List;
import lombok.Data;

@Data
public class BookingDetailDTO {
    private Long bookingId; // cho BE
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerIDCard;
    private String totalAmount;
    private List<DetailDTO> details;
    @Data
    public static class DetailDTO {
        private Long bookingDetailId; // Cho BE
        private String roomNumber;
        private Long roomId;          // cho BE
        private String checkIn;
        private String checkOut;
        private String status;
        private List<ServiceDTO> services;
    }
    
    @Data
    public static class ServiceDTO {
        private Long hotelServiceId; // Cho BE
        private String serviceName;
        private Integer quantity;
        private Double price;
    }
}
