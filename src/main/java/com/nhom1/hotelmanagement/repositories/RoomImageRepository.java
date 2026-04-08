package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.Room;
import com.nhom1.hotelmanagement.entities.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {
    List<RoomImage> findByRoomRoomId(Long roomId);
    void deleteByRoom(Room room);
}
