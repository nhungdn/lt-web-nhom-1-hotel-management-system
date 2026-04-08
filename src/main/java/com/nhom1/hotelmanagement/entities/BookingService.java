package com.nhom1.hotelmanagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class BookingService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingServiceId;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "bookingDetailId")
    private BookingDetail bookingDetail;

    @ManyToOne
    @JoinColumn(name = "serviceId")
    private HotelService service;
}
