
package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Room findByRoomNumber(String roomNumber);
   
}
