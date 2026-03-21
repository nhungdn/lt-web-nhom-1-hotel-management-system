package com.nhom1.hotelmanagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class RoomImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomImageId;

    private String imageUrl;
    private String description;

    @ManyToOne
    @JoinColumn(name = "roomId")
    private Room room;
}
