
package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.BookingDTO;
import com.nhom1.hotelmanagement.dto.BookingDetailDTO;
import com.nhom1.hotelmanagement.dto.CustomerDTO;
import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.entities.Booking;
import com.nhom1.hotelmanagement.entities.BookingDetail;
import com.nhom1.hotelmanagement.entities.BookingHotelService;
import com.nhom1.hotelmanagement.entities.Customer;
import com.nhom1.hotelmanagement.entities.HotelService;
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
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nhom1.hotelmanagement.repositories.BookingHotelServiceRepository;
import com.nhom1.hotelmanagement.repositories.HotelServiceRepository;

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
    
    public List<String> checkRooms(BookingDTO.MultiSubmitRequest request) {
        List<String> errorRooms = new ArrayList<>();
        for (BookingDTO.BookingItem item : request.getBookingItems()) {
            boolean isOpen = checkRoomOpen(item.getRoomNum(), item.getCheckIn(), item.getCheckOut());
            if (!isOpen) {
                errorRooms.add(item.getRoomNum());
            }
        }
        return errorRooms;
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
    
    @Autowired private BookingHotelServiceRepository bookingHotelServiceRepo;
    @Autowired private HotelServiceRepository hotelServiceRepo;
    public List<String> editBooking(BookingDetailDTO request){
        List<String> error = new ArrayList<>();
        for(BookingDetailDTO.DetailDTO detail: request.getDetails() ){
            if(LocalDateTime.parse(detail.getCheckOut()).isBefore(LocalDateTime.now())){
                error.add("Room " + detail.getRoomNumber() + ": check-out date has to be before today!");
                continue;
            }
            if(detail.getCheckIn().compareTo(detail.getCheckOut()) < 0){
                error.add("Room " + detail.getRoomNumber() + ": check-out date has to be after check-in date!");
                continue;
            }
            if(!checkRoomOpen(detail.getRoomNumber(), detail.getCheckIn(), detail.getCheckOut())){
                error.add("Room " + detail.getRoomNumber() + ": this room is not available.");
                continue;
            }
            BookingDetail bd = detailRepo.findById(detail.getBookingDetailId()).orElse(null);
            
            bd.setRoom(roomRepo.findById(detail.getRoomId()).orElse(null));

            bd.setCheckInDate(LocalDateTime.parse(detail.getCheckIn()));
            bd.setCheckOutDate(LocalDateTime.parse(detail.getCheckOut()));
            
            bd.setStatus(detail.getStatus());
            detailRepo.save(bd);
            
            bookingHotelServiceRepo.deleteByBookingDetail(bd);
            for(BookingDetailDTO.ServiceDTO service: detail.getServices()){
                BookingHotelService bhs = new BookingHotelService();
                HotelService hs = hotelServiceRepo.findById(service.getHotelServiceId()).orElse(null);
                bhs.setQuantity(service.getQuantity());
                bhs.setService(hs);
                
                bookingHotelServiceRepo.save(bhs);
            }
        }
        return error;
    }
    
    @Transactional
    public void cancelBooking(Long id, boolean isDetail){
        if (isDetail) {
            BookingDetail bd = detailRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng để hủy"));
            bd.setStatus("CANCELLED");
        } else {
            // Tìm và update toàn bộ phòng thuộc 1 đơn đặt (Booking)
            Booking b = bookingRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt để hủy"));

            List<BookingDetail> details = detailRepo.findAllByBooking(b);
            for (BookingDetail item : details) {
                item.setStatus("CANCELLED");
            }
        }
    }
    
}
