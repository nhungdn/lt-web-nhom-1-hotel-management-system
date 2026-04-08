package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.HotelService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelServiceRepository extends JpaRepository<HotelService, Long> {
}
