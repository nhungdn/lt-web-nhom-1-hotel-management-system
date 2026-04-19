package com.nhom1.hotelmanagement.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ServiceResponse {
    private Long serviceId;
    private String name;
    private BigDecimal price;
}
