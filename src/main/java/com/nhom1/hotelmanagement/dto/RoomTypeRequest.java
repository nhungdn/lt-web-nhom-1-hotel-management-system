package com.nhom1.hotelmanagement.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeRequest {
    private Long roomTypeId;
    private String name;
    private BigDecimal price;
    private String description;
    private List<RoomTypeImageRequest> images = new ArrayList<>();

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public List<RoomTypeImageRequest> getImages() {
        return images;
    }

    public void setImages(List<RoomTypeImageRequest> images) {
        this.images = images;
    }
}
