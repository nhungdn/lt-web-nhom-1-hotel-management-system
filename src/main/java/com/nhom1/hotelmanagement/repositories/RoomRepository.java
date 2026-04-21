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
    long countByStatus(Room.Status status);
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.roomType.roomTypeId = :typeId " +
       "AND r.roomId NOT IN (" +
       "  SELECT bd.room.roomId FROM BookingDetail bd " +
       "  WHERE bd.status NOT IN ('CANCELED', 'COMPLETED') " + // Loại bỏ các đơn đã hủy
       "  AND bd.checkInDate < :end AND bd.checkOutDate > :start" +
       ")")
    int countAvailableRooms(@Param("typeId") Long typeId, 
                             @Param("start") LocalDateTime start, 
                             @Param("end") LocalDateTime end);

    @Query("SELECT r FROM Room r WHERE r.roomType.roomTypeId = :roomTypeId " +
       "AND r.roomId NOT IN (" +
       "  SELECT bd.room.roomId FROM BookingDetail bd " +
<<<<<<< Updated upstream
       "  WHERE bd.status != 'CANCELLED' " +
       "  AND bd.checkInDate < :end AND bd.checkOutDate > :start" +
       ") AND r.status = 'AVAILABLE'")
    List<Room> findAvailableRoomByType(@Param(value = "typeId") Long typeId, 
                                       @Param(value = "start") LocalDateTime start, 
                                       @Param(value = "end") LocalDateTime end);
=======
       "  WHERE bd.status NOT IN ('CANCELED', 'COMPLETED') " +
       "  AND bd.checkInDate < :end AND bd.checkOutDate > :start" +
       ") ORDER BY r.roomNumber ASC") // Order by nằm ở ngoài cùng
    List<Room> findAvailableRoomByType(@Param("roomTypeId") Long roomTypeId, 
                                        @Param("start") LocalDateTime start, 
                                        @Param("end") LocalDateTime end);
>>>>>>> Stashed changes

}
