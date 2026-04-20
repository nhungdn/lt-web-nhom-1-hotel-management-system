package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.Room;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomId(Long roomId);
    List<Room> findByRoomTypeRoomTypeId(Long roomTypeId);
    List<Room> findByStatus(Room.Status status);
    Room findByRoomNumber(String roomNumber);
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.roomType.roomTypeId = :typeId " +
       "AND r.roomId NOT IN (" +
       "  SELECT bd.room.roomId FROM BookingDetail bd JOIN bd.booking b " +
       "  WHERE bd.status != 'CANCELLED' AND r.status = 'AVAILABLE' " +
       "  AND bd.checkInDate < :end AND bd.checkOutDate > :start" +
       ")")
    int countAvailableRooms(@Param(value = "typeId")
            Long typeId, @Param(value = "start")
    LocalDateTime start, @Param(value = "end")
    LocalDateTime end);

    // ── MỚI - dùng cho dashboard ──────────────────────────────────────────────
    long countByStatus(Room.Status status);

}
