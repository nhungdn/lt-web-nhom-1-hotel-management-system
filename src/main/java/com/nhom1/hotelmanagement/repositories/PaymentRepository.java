package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
