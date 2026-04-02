
package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.BookingDTO;
import com.nhom1.hotelmanagement.dto.CustomerDTO;
import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.entities.Booking;
import com.nhom1.hotelmanagement.entities.BookingDetail;
import com.nhom1.hotelmanagement.entities.Customer;
import com.nhom1.hotelmanagement.entities.Room;
import com.nhom1.hotelmanagement.entities.User;
import com.nhom1.hotelmanagement.repositories.BookingDetailRepository;
import com.nhom1.hotelmanagement.repositories.BookingRepository;
import com.nhom1.hotelmanagement.repositories.CustomerRepository;
import com.nhom1.hotelmanagement.repositories.RoomRepository;
import com.nhom1.hotelmanagement.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
    @Autowired private BookingRepository bookingRepo;
    @Autowired private BookingDetailRepository detailRepo;
    @Autowired private RoomRepository roomRepo;
    @Autowired private CustomerRepository customerRepo;
    @Autowired private UserRepository userRepo;
    
    @Transactional
    public CustomerDTO.InfoForBooking checkCustomer(String phone){
        Customer c = customerRepo.findByPhone(phone);
    
        if (c != null) {
            return new CustomerDTO.InfoForBooking(
                    c.getName(), 
                    c.getPhone(), 
                    c.getIdCard()
            );
        }
        return null;
    }
    
    public boolean checkRoomOpen(String roomNum, String checkin, String checkout) {
        long count = detailRepo.countOverlappingBookings(roomNum, checkin, checkout);
        return count == 0; 
    }
    
    public boolean createBooking(BookingDTO.MultiSubmitRequest request, HttpSession session){
        
        LoginResponse ussertemp = (LoginResponse) session.getAttribute("user");
        User user = userRepo.findByUserId(ussertemp.getUserId());
        // Handling customer
        Customer customer = customerRepo.findByPhone(request.getCustomerPhone());
        
        if(customer == null){
            customer = new Customer();
            customer.setName(request.getCustomerName());
            customer.setPhone(request.getCustomerPhone());
            customer.setIdCard(request.getCustomerIdCard());
            customer = customerRepo.save(customer);
        } else {
            customer.setName(request.getCustomerName());
            customer.setPhone(request.getCustomerPhone());
            customer.setIdCard(request.getCustomerIdCard());
            customerRepo.save(customer);
        }
        
        // Check room status
        ArrayList<String> response = new ArrayList<>();
        for(BookingDTO.BookingItem item: request.getBookingItems()){
            boolean isOpen = checkRoomOpen(item.getRoomNum(), item.getCheckIn(), item.getCheckOut());
            if(!isOpen)
                response.add("Phòng " + item.getRoomNum() + " đã có người đặt!");   
        }
        if(!response.isEmpty())
            return false;
        
        //Create booking
        Booking book = new Booking();
        book.setCustomer(customer);
        book.setUser(user);
        bookingRepo.save(book);
        for(BookingDTO.BookingItem item: request.getBookingItems()){
           BookingDetail detail = new BookingDetail();
           Room room = roomRepo.findByRoomNumber(item.getRoomNum());
           detail.setRoom(room);
           detail.setBooking(book);
           detail.setCheckInDate(LocalDateTime.parse(item.getCheckIn()));
           detail.setCheckOutDate(LocalDateTime.parse(item.getCheckOut()));
           detail.setStatus("BOOKED");
           detailRepo.save(detail);
        }
        //
        return true;
    }
}
