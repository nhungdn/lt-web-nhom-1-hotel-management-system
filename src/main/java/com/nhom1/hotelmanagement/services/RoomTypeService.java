package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.RoomTypeRequest;
import com.nhom1.hotelmanagement.dto.RoomTypeResponse;
import com.nhom1.hotelmanagement.entities.RoomType;
import com.nhom1.hotelmanagement.repositories.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomTypeService {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    public List<RoomType> listAll() {
        return roomTypeRepository.findAll();
    }

    public RoomType getById(Long roomTypeId) {
        return roomTypeRepository.findById(roomTypeId).orElse(null);
    }

    public RoomType create(RoomTypeRequest dto) {
        RoomType roomType = new RoomType();
        roomType.setName(dto.getName());
        roomType.setPrice(dto.getPrice());
        roomType.setDescription(dto.getDescription());
        return roomTypeRepository.save(roomType);
    }

    public RoomType update(Long roomTypeId, RoomTypeRequest dto) {
        Optional<RoomType> optionalRoomType = roomTypeRepository.findById(roomTypeId);
        if (!optionalRoomType.isPresent()) {
            return null;
        }

        RoomType roomType = optionalRoomType.get();
        if (dto.getName() != null) {
            roomType.setName(dto.getName());
        }
        if (dto.getPrice() != null) {
            roomType.setPrice(dto.getPrice());
        }
        if (dto.getDescription() != null) {
            roomType.setDescription(dto.getDescription());
        }
        return roomTypeRepository.save(roomType);
    }

    public void delete(Long roomTypeId) {
        roomTypeRepository.deleteById(roomTypeId);
    }

    public List<RoomTypeResponse> listAllDto() {
        return listAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public RoomTypeResponse toDto(RoomType roomType) {
        if (roomType == null) return null;
        RoomTypeResponse dto = new RoomTypeResponse();
        dto.setRoomTypeId(roomType.getRoomTypeId());
        dto.setName(roomType.getName());
        dto.setPrice(roomType.getPrice());
        dto.setDescription(roomType.getDescription());
        return dto;
    }
}