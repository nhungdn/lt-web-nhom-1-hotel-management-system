package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.RoomTypeImageRequest;
import com.nhom1.hotelmanagement.entities.RoomType;
import com.nhom1.hotelmanagement.entities.RoomTypeImage;
import com.nhom1.hotelmanagement.repositories.RoomTypeImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomTypeImageService {
    
    @Autowired
    private RoomTypeImageRepository roomTypeImageRepository;
    
    @Autowired
    private RoomTypeService roomTypeService;
    
    public List<RoomTypeImage> listByRoomTypeId(Long roomTypeId) {
        return roomTypeImageRepository.findByRoomType_RoomTypeId(roomTypeId);
    }
    
    public RoomTypeImageRequest toDto(RoomTypeImage entity) {
        RoomTypeImageRequest dto = new RoomTypeImageRequest();
        dto.setRoomTypeImageId(entity.getRoomTypeImageId());
        dto.setImageUrl(entity.getImageUrl());
        dto.setDescription(entity.getDescription());
        return dto;
    }
    
    public List<RoomTypeImageRequest> listByRoomTypeIdAsDto(Long roomTypeId) {
        return listByRoomTypeId(roomTypeId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public RoomTypeImage create(Long roomTypeId, RoomTypeImageRequest dto) {
        RoomType roomType = roomTypeService.getById(roomTypeId);
        if (roomType == null) {
            throw new IllegalArgumentException("RoomType not found");
        }
        
        RoomTypeImage entity = new RoomTypeImage();
        entity.setRoomType(roomType);
        entity.setImageUrl(dto.getImageUrl());
        entity.setDescription(dto.getDescription());
        
        return roomTypeImageRepository.save(entity);
    }
    
    public RoomTypeImage update(Long imageId, RoomTypeImageRequest dto) {
        RoomTypeImage entity = roomTypeImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found"));
        
        entity.setImageUrl(dto.getImageUrl());
        entity.setDescription(dto.getDescription());
        
        return roomTypeImageRepository.save(entity);
    }
    
    public void delete(Long imageId) {
        roomTypeImageRepository.deleteById(imageId);
    }
    
    public RoomTypeImage getById(Long imageId) {
        return roomTypeImageRepository.findById(imageId).orElse(null);
    }
}
