
package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.BookingRequest;
import com.nhom1.hotelmanagement.entities.Booking;
import com.nhom1.hotelmanagement.entities.BookingDetail;
import com.nhom1.hotelmanagement.entities.Customer;
import com.nhom1.hotelmanagement.repositories.BookingDetailRepository;
import com.nhom1.hotelmanagement.repositories.BookingRepository;
import com.nhom1.hotelmanagement.repositories.CustomerRepository;
import com.nhom1.hotelmanagement.repositories.RoomRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
    @Autowired private BookingRepository bookingRepo;
    @Autowired private BookingDetailRepository detailRepo;
    @Autowired private RoomRepository roomRepo;
    @Autowired private CustomerRepository customerRepo;
    
    @Transactional
    public Booking createBooking(BookingRequest dto){
        Customer customer;
        
        if (dto.getCustomerID() != null) {
        // Khách cũ: Lấy từ DB
        customer = customerRepo.findById(dto.getCustomerID()).get();
        } else {
            // Khách mới: Tạo mới hoàn toàn
            customer = new Customer();
            customer.setName(dto.getCustomerName());
            customer.setPhone(dto.getCustomerPhone());
            customer.setIdCard(dto.getCustomerIdCard());
            customer = customerRepo.save(customer); // Lưu khách trước để có ID
        }
        
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setCheckInDate(LocalDateTime.parse(dto.getCheckIn()));
        booking.setCheckOutDate(LocalDateTime.parse(dto.getCheckOut()));
        booking.setStatus("PENDING");
        
        Booking savedBooking = bookingRepo.save(booking);
        for(long roomId: dto.getRoomIds()){
            BookingDetail detail = new BookingDetail();
            detail.setBooking(savedBooking);
            detail.setRoom(roomRepo.findById(roomId).get());
            detailRepo.save(detail);
        }
        return savedBooking;
    }
}
