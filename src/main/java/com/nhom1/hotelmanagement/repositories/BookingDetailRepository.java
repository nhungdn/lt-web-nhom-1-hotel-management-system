
package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long>{
    
}
