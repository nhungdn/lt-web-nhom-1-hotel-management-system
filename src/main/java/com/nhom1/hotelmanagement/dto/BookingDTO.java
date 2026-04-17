
package com.nhom1.hotelmanagement.dto;

import java.util.List;
import lombok.Data;

public class BookingDTO {
    
    //class submit full form booking
    @Data
    public static class MultiSubmitRequest{
        private String customerName;
        private String customerPhone;
        private String customerIdCard;
        
        private List<BookingItem> bookingItems;
    }
    
    @Data
    public static class BookingItem{
        private String roomNum;
        private String checkIn;
        private String checkOut;
    }
    
    @Data
    public static class FilterDate{
        private String checkIn;
        private String checkOut;
    }
}
