package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.RoomImageRequest;
import com.nhom1.hotelmanagement.dto.RoomImageResponse;
import com.nhom1.hotelmanagement.entities.Room;
import com.nhom1.hotelmanagement.entities.RoomImage;
import com.nhom1.hotelmanagement.repositories.RoomImageRepository;
import com.nhom1.hotelmanagement.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomImageService {

    @Autowired
    private RoomImageRepository roomImageRepository;

    @Autowired
    private RoomRepository roomRepository;

    public List<RoomImage> listAll() {
        return roomImageRepository.findAll();
    }

    public RoomImage getById(Long roomImageId) {
        return roomImageRepository.findById(roomImageId).orElse(null);
    }

    public List<RoomImage> listByRoomId(Long roomId) {
        return roomImageRepository.findAll().stream()
                .filter(img -> img.getRoom() != null && img.getRoom().getRoomId().equals(roomId))
                .collect(Collectors.toList());
    }

    public RoomImage create(RoomImageRequest dto) {
        RoomImage roomImage = new RoomImage();
        roomImage.setImageUrl(dto.getImageUrl());
        roomImage.setDescription(dto.getDescription());

        if (dto.getRoomId() != null) {
            Optional<Room> room = roomRepository.findByRoomId(dto.getRoomId());
            room.ifPresent(roomImage::setRoom);
        }

        return roomImageRepository.save(roomImage);
    }

    public RoomImage update(Long roomImageId, RoomImageRequest dto) {
        Optional<RoomImage> optionalRoomImage = roomImageRepository.findById(roomImageId);
        if (!optionalRoomImage.isPresent()) {
            return null;
        }

        RoomImage roomImage = optionalRoomImage.get();
        if (dto.getImageUrl() != null) {
            roomImage.setImageUrl(dto.getImageUrl());
        }
        if (dto.getDescription() != null) {
            roomImage.setDescription(dto.getDescription());
        }
        if (dto.getRoomId() != null) {
            roomRepository.findByRoomId(dto.getRoomId()).ifPresent(roomImage::setRoom);
        }
        return roomImageRepository.save(roomImage);
    }

    public void delete(Long roomImageId) {
        roomImageRepository.deleteById(roomImageId);
    }

    public List<RoomImageResponse> listAllDto() {
        return listAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public RoomImageResponse toDto(RoomImage roomImage) {
        if (roomImage == null) return null;
        RoomImageResponse dto = new RoomImageResponse();
        dto.setRoomImageId(roomImage.getRoomImageId());
        dto.setImageUrl(roomImage.getImageUrl());
        dto.setDescription(roomImage.getDescription());
        dto.setRoomId(roomImage.getRoom() == null ? null : roomImage.getRoom().getRoomId());
        return dto;
    }
}