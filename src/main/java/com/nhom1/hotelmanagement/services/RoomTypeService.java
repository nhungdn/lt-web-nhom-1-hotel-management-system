package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.RoomTypeRequest;
import com.nhom1.hotelmanagement.dto.RoomTypeResponse;
import com.nhom1.hotelmanagement.dto.RoomTypeImageRequest;
import com.nhom1.hotelmanagement.entities.RoomType;
import com.nhom1.hotelmanagement.entities.RoomTypeImage;
import com.nhom1.hotelmanagement.repositories.RoomRepository;
import com.nhom1.hotelmanagement.repositories.RoomTypeRepository;
import com.nhom1.hotelmanagement.repositories.RoomTypeImageRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomTypeService {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private RoomTypeImageRepository roomTypeImageRepository;

    @Autowired
    private RoomRepository roomRepository;

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
        
        RoomType savedRoomType = roomTypeRepository.save(roomType);
        
        // Save associated images if provided
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (RoomTypeImageRequest imageRequest : dto.getImages()) {
                if (imageRequest.getImageUrl() != null && !imageRequest.getImageUrl().trim().isEmpty()) {
                    RoomTypeImage roomTypeImage = new RoomTypeImage();
                    roomTypeImage.setImageUrl(imageRequest.getImageUrl());
                    roomTypeImage.setDescription(imageRequest.getDescription());
                    roomTypeImage.setRoomType(savedRoomType);
                    roomTypeImageRepository.save(roomTypeImage);
                }
            }
        }
        
        return savedRoomType;
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
        RoomType savedRoomType = roomTypeRepository.save(roomType);
        
        // Save new associated images if provided
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (RoomTypeImageRequest imageRequest : dto.getImages()) {
                if (imageRequest.getImageUrl() != null && !imageRequest.getImageUrl().trim().isEmpty()) {
                    RoomTypeImage roomTypeImage = new RoomTypeImage();
                    roomTypeImage.setImageUrl(imageRequest.getImageUrl());
                    roomTypeImage.setDescription(imageRequest.getDescription());
                    roomTypeImage.setRoomType(savedRoomType);
                    roomTypeImageRepository.save(roomTypeImage);
                }
            }
        }
        
        return savedRoomType;
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
        // Count total rooms with this room type
        int totalRooms = roomRepository.findByRoomTypeRoomTypeId(roomType.getRoomTypeId()).size();
        dto.setTotalRooms(totalRooms);
        return dto;
    }
    
    
    public List<RoomTypeResponse> filterAvailableRooms(String start, String end) {
        LocalDate startD = LocalDate.parse(start);
        LocalDate endD = LocalDate.parse(end);
        
        LocalDateTime startTime = startD.atTime(12, 0);
        LocalDateTime endTime = endD.atTime(8, 0);
        // Lấy tất cả RoomTypes
        List<RoomType> allTypes = roomTypeRepository.findAll();

        return allTypes.stream().map(type -> {
            RoomTypeResponse dto = toDto(type);

            // Tính số phòng còn trống thực tế trong DB
            int availRoom = roomRepository.countAvailableRooms(type.getRoomTypeId(), startTime, endTime);

            dto.setAvailableRooms(Math.max(0, availRoom));
            return dto;
        }).collect(Collectors.toList());
    }
}