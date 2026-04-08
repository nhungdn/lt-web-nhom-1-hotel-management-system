package com.nhom1.hotelmanagement.entities;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "roomtypes")
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "description", length = 500)
    private String description;

    public RoomType() {
    }

    public RoomType(String name, BigDecimal price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

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
        return price != null ? price : null;
    }

    public void setPrice(BigDecimal price) {
        this.price = price != null ? new BigDecimal(price.toString()) : null;
    }

    public BigDecimal getPriceAsBigDecimal() {
        return price;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
