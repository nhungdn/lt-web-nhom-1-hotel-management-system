
package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.BookingDetail;
import com.nhom1.hotelmanagement.entities.BookingHotelService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingHotelServiceRepository extends JpaRepository<BookingHotelService, Long>{

    public void deleteByBookingDetail(BookingDetail bd);

}
