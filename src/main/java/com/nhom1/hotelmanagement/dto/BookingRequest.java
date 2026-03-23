
package com.nhom1.hotelmanagement.dto;

import java.util.List;

public class BookingRequest {
    private Long customerID;
    private String customerName;
    private String customerPhone;
    private String customerIdCard;
    private List<Long> roomIds;
    private String checkIn;
    private String checkOut;

    public BookingRequest(Long customerID, String customerName, String customerPhone, String customerIdCard, List<Long> roomIds, String checkIn, String checkOut) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerIdCard = customerIdCard;
        this.roomIds = roomIds;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public Long getCustomerID() {
        return customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getCustomerIdCard() {
        return customerIdCard;
    }

    public List<Long> getRoomIds() {
        return roomIds;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    
    
}
