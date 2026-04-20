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

    long countByStatus(String status);

    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p WHERE p.status = :status")
    BigDecimal sumByStatus(@Param("status") String status);

    @Query("SELECT p FROM Payment p " +
            "JOIN FETCH p.booking bk " +
            "JOIN FETCH bk.customer " +
            "ORDER BY p.paymentId DESC")
    List<Payment> findRecentPayments(Pageable pageable);
}