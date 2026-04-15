package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.RoomTypeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomTypeImageRepository extends JpaRepository<RoomTypeImage, Long> {
    List<RoomTypeImage> findByRoomType_RoomTypeId(Long roomTypeId);
}
