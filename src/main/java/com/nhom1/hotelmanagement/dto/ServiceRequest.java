package com.nhom1.hotelmanagement.dto;

import java.math.BigDecimal;

public class ServiceRequest {
    private Long serviceId;
    private String name;
    private BigDecimal price;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
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
}
