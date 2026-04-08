
package com.nhom1.hotelmanagement.dto;

import java.math.BigDecimal;

import com.nhom1.hotelmanagement.entities.Room.Status;

public class RoomStatDTO {
    private String roomTypeName;
    private String roomDesc;
    private String roomNumber;
    private Status status;
    private BigDecimal price;
    private String checkIn;
    private String checkOut;
    private String cusName;
    private String cusSDT;
    private String cusIdCard;

    public RoomStatDTO() {
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public String getRoomDesc() {
        return roomDesc;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public Status getStatus() {
        return status;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public String getCusName() {
        return cusName;
    }

    public String getCusSDT() {
        return cusSDT;
    }

    public String getCusIdCard() {
        return cusIdCard;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public void setRoomDesc(String roomDesc) {
        this.roomDesc = roomDesc;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }

    public void setCusSDT(String cusSDT) {
        this.cusSDT = cusSDT;
    }

    public void setCusIdCard(String cusIdCard) {
        this.cusIdCard = cusIdCard;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    
}
