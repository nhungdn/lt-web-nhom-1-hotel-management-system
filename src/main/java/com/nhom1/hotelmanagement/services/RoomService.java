
package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.RoomStatDTO;
import com.nhom1.hotelmanagement.entities.BookingDetail;
import com.nhom1.hotelmanagement.entities.Room;
import com.nhom1.hotelmanagement.repositories.BookingDetailRepository;
import com.nhom1.hotelmanagement.repositories.RoomRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
    @Autowired private RoomRepository roomRepo;
    @Autowired private BookingDetailRepository detailRepo;
    public List<RoomStatDTO> getFullRoomList() {
        List<Room> allRooms = roomRepo.findAll();
        List<RoomStatDTO> dashboard = new ArrayList<>();

        for (Room r : allRooms) {
            //Getting latest booking stat
            List<BookingDetail> bookings = detailRepo.findCurrentOrUpcoming(
                    r.getRoomNumber(), PageRequest.of(0, 1)
            );

            RoomStatDTO dto = new RoomStatDTO();
            dto.setRoomNumber(r.getRoomNumber());
            dto.setRoomTypeName(r.getRoomType().getName());
            dto.setRoomDesc(r.getRoomType().getDescription());
            dto.setPrice(r.getRoomType().getPrice());
            dto.setStatus(r.getStatus());
            
            if (!bookings.isEmpty()) {
                BookingDetail current = bookings.get(0);
                dto.setCheckIn(current.getCheckInDate().toString());
                dto.setCheckOut(current.getCheckOutDate().toString());
                dto.setCusName(current.getBooking().getCustomer().getName());
                dto.setCusSDT(current.getBooking().getCustomer().getPhone());
                dto.setCusIdCard(current.getBooking().getCustomer().getIdCard());
            }
            dashboard.add(dto);
        }
        return dashboard;
    }
    
}
