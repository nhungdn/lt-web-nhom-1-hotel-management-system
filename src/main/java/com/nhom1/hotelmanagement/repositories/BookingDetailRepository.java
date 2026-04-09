
package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.Booking;
import com.nhom1.hotelmanagement.entities.BookingDetail;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long>{
    @Query("SELECT COUNT(b) FROM BookingDetail b JOIN b.room r " +
           "WHERE r.roomNumber = :roomNum " +
           "AND b.checkInDate < :checkOut " +
           "AND b.checkOutDate > :checkIn " +
           "AND b.status != 'CANCELLED'") // Bỏ qua các đơn đã hủy
    long countOverlappingBookings(String roomNum, String checkIn, String checkOut);
    @Query("SELECT bd FROM BookingDetail bd " +
       "JOIN bd.room r " +
       "WHERE r.roomNumber = :roomNum " +
       "AND bd.status NOT IN ('CANCELLED', 'COMPLETED') " +
       "AND bd.checkOutDate > CURRENT_TIMESTAMP " +
       "ORDER BY bd.checkInDate ASC")
    List<BookingDetail> findCurrentOrUpcoming(String roomNum, Pageable pageable);

    public List<BookingDetail> findAllByBooking(Booking b);

}
