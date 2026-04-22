
package com.nhom1.hotelmanagement.dto;

import java.util.List;
import lombok.Data;

public class BookingDTO {
    
    //class submit full form booking
    @Data
    public static class MultiSubmitRequest{
        private String customerName;
        private String customerPhone;
        private String customerEmail;
        private String customerIdCard;
        
        private List<BookingItem> bookingItems;
    }
    
    @Data
    public static class BookingItem{
        private Long roomTypeId;
        private int quantity;
        private String checkIn;
        private String checkOut;
        private List<ServiceItem> serviceItems;
    }
    
    @Data
    public static class ServiceItem{
        private Long serviceId;
        private int quantity;
    }
    
    @Data
    public static class FilterDate{
        private String checkIn;
        private String checkOut;
    }
    @Data
    public static class CancelBook{
        private Long id;
        private boolean isDetail;
    }
    
    @Data
    public static class StatusDTO{
        private Long id;
        private String status;
    }
}
