
package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.Booking;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT DISTINCT b FROM Booking b JOIN b.bookingDetails d " +
            "WHERE (MONTH(d.checkInDate) = :month AND YEAR(d.checkInDate) = :year) " +
            "OR (MONTH(d.checkOutDate) = :month AND YEAR(d.checkOutDate) = :year) " +
            "ORDER BY b.id DESC")
    List<Booking> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
}
