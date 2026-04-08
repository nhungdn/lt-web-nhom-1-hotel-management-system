package com.nhom1.hotelmanagement.dto;

import java.util.ArrayList;
import java.util.List;

public class RoomRequest {
    private String roomNumber;
    private String status;
    private Long roomTypeId;
    //private List<String> imageUrls = new ArrayList<>();

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    // public List<String> getImageUrls() {
    //     return imageUrls;
    // }

    // public void setImageUrls(List<String> imageUrls) {
    //     this.imageUrls = imageUrls;
    // }
}