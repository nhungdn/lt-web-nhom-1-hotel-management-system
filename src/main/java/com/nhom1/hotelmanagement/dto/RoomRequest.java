package com.nhom1.hotelmanagement.dto;

import java.util.ArrayList;
import java.util.List;

public class RoomRequest {
    private Long roomId;
    private String roomNumber;
    private String status;
    private Long roomTypeId;
    private List<RoomImageRequest> images = new ArrayList<>();

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

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

    public List<RoomImageRequest> getImages() {
        return images;
    }

    public void setImages(List<RoomImageRequest> images) {
        this.images = images;
    }
}