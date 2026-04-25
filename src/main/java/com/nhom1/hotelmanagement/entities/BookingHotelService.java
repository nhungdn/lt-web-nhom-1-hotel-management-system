package com.nhom1.hotelmanagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="BookingService")
@Getter @Setter
@NoArgsConstructor
public class BookingHotelService {

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

    @Column(name = "added_at")
    private LocalDateTime addedAt;
}
