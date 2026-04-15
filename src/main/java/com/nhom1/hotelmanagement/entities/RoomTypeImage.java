package com.nhom1.hotelmanagement.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "room_type_image")
public class RoomTypeImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_type_image_id")
    private Long roomTypeImageId;
    
    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "description")
    private String description;
    
    public RoomTypeImage() {}
    
    public RoomTypeImage(RoomType roomType, String imageUrl, String description) {
        this.roomType = roomType;
        this.imageUrl = imageUrl;
        this.description = description;
    }
    
    public Long getRoomTypeImageId() {
        return roomTypeImageId;
    }
    
    public void setRoomTypeImageId(Long roomTypeImageId) {
        this.roomTypeImageId = roomTypeImageId;
    }
    
    public RoomType getRoomType() {
        return roomType;
    }
    
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
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
