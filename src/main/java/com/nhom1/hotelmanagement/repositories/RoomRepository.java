package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.Room;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomId(Long roomId);
    List<Room> findByRoomTypeRoomTypeId(Long roomTypeId);
    List<Room> findByStatus(Room.Status status);
    Room findByRoomNumber(String roomNumber);
}
