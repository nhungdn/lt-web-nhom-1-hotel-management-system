
package com.nhom1.hotelmanagement.dto;

import java.util.List;

public class BookingDTO {
    
    //class submit full form booking
    public static class MultiSubmitRequest{
        private String customerName;
        private String customerPhone;
        private String customerIdCard;
        
        private List<BookingItem> bookingItems;

        public MultiSubmitRequest(String customerName, String customerPhone, String customerIdCard, List<BookingItem> bookingItems) {
            this.customerName = customerName;
            this.customerPhone = customerPhone;
            this.customerIdCard = customerIdCard;
            this.bookingItems = bookingItems;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustomerPhone() {
            return customerPhone;
        }

        public void setCustomerPhone(String customerPhone) {
            this.customerPhone = customerPhone;
        }

        public String getCustomerIdCard() {
            return customerIdCard;
        }

        public void setCustomerIdCard(String customerIdCard) {
            this.customerIdCard = customerIdCard;
        }

        public List<BookingItem> getBookingItems() {
            return bookingItems;
        }

        public void setBookingItems(List<BookingItem> bookingItems) {
            this.bookingItems = bookingItems;
        }
        
    }
    
    public static class BookingItem{
        private String roomNum;
        private String checkIn;
        private String checkOut;

        public BookingItem(String roomNum, String checkIn, String checkOut) {
            this.roomNum = roomNum;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
        }

        public String getRoomNum() {
            return roomNum;
        }

        public void setRoomNum(String roomNum) {
            this.roomNum = roomNum;
        }

        public String getCheckIn() {
            return checkIn;
        }

        public void setCheckIn(String checkIn) {
            this.checkIn = checkIn;
        }

        public String getCheckOut() {
            return checkOut;
        }

        public void setCheckOut(String checkOut) {
            this.checkOut = checkOut;
        }
        
        
    }
    
    
    //class edit booking
    public class EditRequest{
        
    }
    
    //class delete booking
    public class DeleteRequest{
        
    }
}
