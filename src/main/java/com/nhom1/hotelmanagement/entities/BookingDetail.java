package com.nhom1.hotelmanagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

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
    
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;

    private String status;
    private Double priceAtBooking;

    public Long getBookingDetailId() {
        return bookingDetailId;
    }

    public void setBookingDetailId(Long bookingDetailId) {
        this.bookingDetailId = bookingDetailId;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
    
    public LocalDateTime getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDateTime checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDateTime getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDateTime checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getPriceAtBooking() {
        return priceAtBooking;
    }

    public void setPriceAtBooking(Double priceAtBooking) {
        this.priceAtBooking = priceAtBooking;
    }
    
}
