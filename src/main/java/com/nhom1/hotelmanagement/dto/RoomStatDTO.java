
package com.nhom1.hotelmanagement.dto;

import java.math.BigDecimal;

import com.nhom1.hotelmanagement.entities.Room.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class RoomStatDTO {
    private String roomTypeName;
    private String roomDesc;
    private String roomNumber;
    private Status status;
    private BigDecimal price;
    private String checkIn;
    private String checkOut;
    private String cusName;
    private String cusSDT;
    private String cusIdCard;
    
}
