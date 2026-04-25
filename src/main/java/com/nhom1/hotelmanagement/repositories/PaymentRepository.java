package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

        List<Payment> findByBookingDetail_Booking_BookingId(Long bookingId);

        List<Payment> findByBookingDetail_Booking_BookingIdAndStatusIgnoreCase(Long bookingId, String status);

        List<Payment> findByBookingDetail_BookingDetailId(Long bookingDetailId);

    long countByStatus(String status);

    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p WHERE p.status = :status")
    BigDecimal sumByStatus(@Param("status") String status);

    @Query("SELECT p FROM Payment p ORDER BY p.paymentId DESC")
    List<Payment> findRecentPayments(Pageable pageable);

    // Doanh thu theo từng ngày trong 1 tháng cụ thể
    @Query("SELECT DAY(p.paymentDate), SUM(p.totalAmount) " +
            "FROM Payment p " +
            "WHERE p.status = 'PAID' " +
            "AND YEAR(p.paymentDate) = :year " +
            "AND MONTH(p.paymentDate) = :month " +
            "GROUP BY DAY(p.paymentDate) " +
            "ORDER BY DAY(p.paymentDate)")
    List<Object[]> revenueByDay(@Param("year") int year,
                                @Param("month") int month);

    // Doanh thu theo từng tháng trong 1 năm
    @Query("SELECT MONTH(p.paymentDate), SUM(p.totalAmount) " +
            "FROM Payment p " +
            "WHERE p.status = 'PAID' " +
            "AND YEAR(p.paymentDate) = :year " +
            "GROUP BY MONTH(p.paymentDate) " +
            "ORDER BY MONTH(p.paymentDate)")
    List<Object[]> revenueByMonth(@Param("year") int year);
}
