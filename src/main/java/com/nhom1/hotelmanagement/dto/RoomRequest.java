package com.nhom1.hotelmanagement.dto;

public class RoomRequest {
    private String roomNumber;
    private String status;
    private Long roomTypeId;

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getStatus() {
        return status;
    }

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }
}