package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.RoomRequest;
import com.nhom1.hotelmanagement.dto.RoomImageRequest;
import com.nhom1.hotelmanagement.dto.RoomResponse;
import com.nhom1.hotelmanagement.dto.RoomStatDTO;
import com.nhom1.hotelmanagement.entities.BookingDetail;
import com.nhom1.hotelmanagement.entities.Room;
import com.nhom1.hotelmanagement.entities.RoomImage;
import com.nhom1.hotelmanagement.entities.RoomType;
import com.nhom1.hotelmanagement.repositories.RoomRepository;
import com.nhom1.hotelmanagement.repositories.BookingDetailRepository;
import com.nhom1.hotelmanagement.repositories.RoomImageRepository;
import com.nhom1.hotelmanagement.repositories.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private RoomImageRepository roomImageRepository;

    public List<Room> listAll() {
        return roomRepository.findAll();
    }

    public List<Room> listAvailable() {
        return roomRepository.findByStatus(Room.Status.AVAILABLE);
    }

    public Room getById(Long roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }

    public Room create(RoomRequest request) {
        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setStatus(Room.Status.valueOf(request.getStatus() == null ? "AVAILABLE" : request.getStatus()));

        if (request.getRoomTypeId() != null) {
            Optional<RoomType> roomType = roomTypeRepository.findById(request.getRoomTypeId());
            roomType.ifPresent(room::setRoomType);
        }

        // Save room first
        Room savedRoom = roomRepository.save(room);

        // Then create associated images if provided
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (RoomImageRequest imageRequest : request.getImages()) {
                // Only create image if both URL and description are not empty
                if (imageRequest.getImageUrl() != null && !imageRequest.getImageUrl().trim().isEmpty()) {
                    RoomImage roomImage = new RoomImage();
                    roomImage.setImageUrl(imageRequest.getImageUrl());
                    roomImage.setDescription(imageRequest.getDescription());
                    roomImage.setRoom(savedRoom);
                    roomImageRepository.save(roomImage);
                }
            }
        }

        return savedRoom;
    }

    public Room update(Long roomId, RoomRequest request) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (!optionalRoom.isPresent()) {
            return null;
        }

        Room room = optionalRoom.get();
        if (request.getRoomNumber() != null) {
            room.setRoomNumber(request.getRoomNumber());
        }
        if (request.getStatus() != null) {
            room.setStatus(Room.Status.valueOf(request.getStatus()));
        }
        if (request.getRoomTypeId() != null) {
            roomTypeRepository.findById(request.getRoomTypeId()).ifPresent(room::setRoomType);
        }
        return roomRepository.save(room);
    }

    public void delete(Long roomId) {
        roomRepository.deleteById(roomId);
    }

    public List<RoomResponse> listAllDto() {
        return listAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public RoomResponse toDto(Room room) {
        if (room == null) return null;

        RoomResponse dto = new RoomResponse();
        dto.setRoomId(room.getRoomId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setStatus(room.getStatus() == null ? null : room.getStatus().name());
        dto.setRoomTypeId(room.getRoomType() == null ? null : room.getRoomType().getRoomTypeId());
        dto.setRoomTypeName(room.getRoomType() == null ? null : room.getRoomType().getName());
        dto.setPrice(room.getRoomType() == null ? null : room.getRoomType().getPrice());
        dto.setRoomDescription(room.getRoomType() == null ? null : room.getRoomType().getDescription());
        return dto;
    }

    @Autowired private BookingDetailRepository detailRepo;
    public List<RoomStatDTO> getFullRoomList() {
        List<Room> allRooms = roomRepository.findAll();
        List<RoomStatDTO> dashboard = new ArrayList<>();

        for (Room r : allRooms) {
            //Getting latest booking stat
            List<BookingDetail> bookings = detailRepo.findCurrentOrUpcoming(
                    r.getRoomNumber(), PageRequest.of(0, 1)
            );

            RoomStatDTO dto = new RoomStatDTO();
            dto.setRoomNumber(r.getRoomNumber());
            dto.setRoomTypeName(r.getRoomType().getName());
            dto.setRoomDesc(r.getRoomType().getDescription());
            dto.setPrice(r.getRoomType().getPrice());
            dto.setStatus(r.getStatus());
            
            if (!bookings.isEmpty()) {
                BookingDetail current = bookings.get(0);
                dto.setCheckIn(current.getCheckInDate().toString());
                dto.setCheckOut(current.getCheckOutDate().toString());
                dto.setCusName(current.getBooking().getCustomer().getName());
                dto.setCusSDT(current.getBooking().getCustomer().getPhone());
                dto.setCusIdCard(current.getBooking().getCustomer().getIdCard());
            }
            dashboard.add(dto);
        }
        return dashboard;
    }
}
