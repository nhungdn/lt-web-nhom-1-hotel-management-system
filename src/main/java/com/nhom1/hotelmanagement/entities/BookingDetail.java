package com.nhom1.hotelmanagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class BookingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingDetailId;

    @ManyToOne
    @JoinColumn(name = "bookingId")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "roomId")
    private Room room;
}
