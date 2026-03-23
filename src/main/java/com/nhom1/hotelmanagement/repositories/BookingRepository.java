
package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long>{
    
}
