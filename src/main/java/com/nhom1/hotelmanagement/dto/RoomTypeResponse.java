package com.nhom1.hotelmanagement.dto;

import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeResponse {
    private Long roomTypeId;
    private String name;
    private BigDecimal price;
    private String description;
    private Integer totalRooms;
}
