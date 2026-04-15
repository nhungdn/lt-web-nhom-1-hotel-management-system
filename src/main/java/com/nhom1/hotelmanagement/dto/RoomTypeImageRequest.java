package com.nhom1.hotelmanagement.dto;

public class RoomTypeImageRequest {
    private Long roomTypeImageId;
    private String imageUrl;
    private String description;
    
    public RoomTypeImageRequest() {}
    
    public RoomTypeImageRequest(String imageUrl, String description) {
        this.imageUrl = imageUrl;
        this.description = description;
    }
    
    public Long getRoomTypeImageId() {
        return roomTypeImageId;
    }
    
    public void setRoomTypeImageId(Long roomTypeImageId) {
        this.roomTypeImageId = roomTypeImageId;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
