package com.nhom1.hotelmanagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private Double totalAmount;
    private LocalDateTime paymentDate;
    private String status;

    @OneToOne
    @JoinColumn(name = "bookingId")
    private Booking booking;
}
