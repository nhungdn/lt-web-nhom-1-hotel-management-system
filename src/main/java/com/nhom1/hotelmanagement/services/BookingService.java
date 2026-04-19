
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
import com.nhom1.hotelmanagement.entities.RoomType;
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
import com.nhom1.hotelmanagement.repositories.RoomTypeRepository;
import java.time.LocalDate;

@Service
public class BookingService {
    @Autowired private BookingRepository bookingRepo;
    @Autowired private BookingDetailRepository detailRepo;
    @Autowired private RoomRepository roomRepo;
    @Autowired private RoomTypeRepository roomTypeRepo;
    @Autowired private CustomerRepository customerRepo;
    @Autowired private UserRepository userRepo;
    
    public int checkCustomer(String phone, String idCard, String email) {
        if (customerRepo.existsByPhone(phone)) return 1;
        if (customerRepo.existsByEmail(email)) return 2;
        if (customerRepo.existsByIdCard(idCard)) return 3;
        return 0;
    }
    
    @Transactional
    public List<String> createBooking(BookingDTO.MultiSubmitRequest request, HttpSession session){
        
        LoginResponse ussertemp = (LoginResponse) session.getAttribute("user");
        User user = userRepo.findByUserId(ussertemp.getUserId());
        
        // Handling customer
        Customer customer = customerRepo.findByPhone(request.getCustomerPhone());
        if (customer == null) customer = new Customer();
        
        customer.setName(request.getCustomerName());
        customer.setPhone(request.getCustomerPhone());
        customer.setIdCard(request.getCustomerIdCard());
        customer.setEmail(request.getCustomerEmail());
        customer = customerRepo.save(customer);
        
        List<String> error = new ArrayList<>();
        
        // 2. Kiểm tra tính khả dụng của TOÀN BỘ phòng trước khi tạo Booking
        for (BookingDTO.BookingItem item : request.getBookingItems()) {
            LocalDateTime startTime = LocalDate.parse(item.getCheckIn()).atTime(12, 0);
            LocalDateTime endTime = LocalDate.parse(item.getCheckOut()).atTime(8, 0);
            
            List<Room> availRoom = roomRepo.findAvailableRoomByType(item.getRoomTypeId(), startTime, endTime);
            if (availRoom.size() < item.getQuantity()) {
                RoomType rt = roomTypeRepo.findById(item.getRoomTypeId()).orElse(null);
                error.add("Loại phòng " + (rt != null ? rt.getName() : "không xác định") + " không đủ phòng trống.");
            }
        }

        // Nếu có bất kỳ lỗi thiếu phòng nào, trả về luôn và không lưu vào DB (nhờ @Transactional)
        if (!error.isEmpty()) return error;

        // 3. Tiến hành lưu Booking và Details
        Booking book = new Booking();
        book.setCustomer(customer);
        book.setUser(user);
        bookingRepo.save(book);

        for (BookingDTO.BookingItem item : request.getBookingItems()) {
            LocalDateTime startTime = LocalDate.parse(item.getCheckIn()).atTime(12, 0);
            LocalDateTime endTime = LocalDate.parse(item.getCheckOut()).atTime(8, 0);
            List<Room> availRoom = roomRepo.findAvailableRoomByType(item.getRoomTypeId(), startTime, endTime);

            for (int i = 0; i < item.getQuantity(); i++) {
                Room room = availRoom.get(i);
                
                BookingDetail detail = new BookingDetail();
                detail.setRoom(room);
                detail.setBooking(book);
                detail.setCheckInDate(startTime);
                detail.setCheckOutDate(endTime);
                detail.setStatus("BOOKED");
                detail = detailRepo.save(detail);

                final BookingDetail savedDetail = detailRepo.save(detail);
                // 4. Lưu Dịch vụ đi kèm
                if (item.getServiceItems() != null) {
                    for (BookingDTO.ServiceItem sDto : item.getServiceItems()) {
                        hotelServiceRepo.findById(sDto.getServiceId()).ifPresent(hs -> {
                            BookingHotelService bhs = new BookingHotelService();
                            bhs.setBookingDetail(savedDetail);
                            bhs.setService(hs);
                            bhs.setQuantity(sDto.getQuantity());
                            bookingHotelServiceRepo.save(bhs);
                        });
                    }
                }
            }
        }
        return error; // Lúc này error sẽ rỗng
    }
    
    
    @Autowired private BookingHotelServiceRepository bookingHotelServiceRepo;
    @Autowired private HotelServiceRepository hotelServiceRepo;
    public List<String> editBooking(BookingDetailDTO request) {
        List<String> error = new ArrayList<>();
        for (BookingDetailDTO.DetailDTO detail : request.getDetails()) {
            LocalDateTime start = LocalDateTime.parse(detail.getCheckIn());
            LocalDateTime end = LocalDateTime.parse(detail.getCheckOut());

            // Validate ngày tháng
            if (end.isBefore(start)) {
                error.add("Phòng " + detail.getRoomNumber() + ": Ngày trả phải sau ngày nhận!");
                continue;
            }

            // Kiểm tra phòng trống (Loại trừ ID của chính BookingDetail này)
            boolean isAvailable = detailRepo.checkAvailabilityExcludeCurrent(
                    detail.getRoomId(), start, end, detail.getBookingDetailId());

            if (!isAvailable) {
                error.add("Phòng " + detail.getRoomNumber() + ": Không còn phòng trống trong thời gian này.");
                continue;
            }

            // Update thông tin
            BookingDetail bd = detailRepo.findById(detail.getBookingDetailId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy detail"));

            bd.setRoom(roomRepo.findById(detail.getRoomId()).orElse(null));
            bd.setCheckInDate(start);
            bd.setCheckOutDate(end);
            bd.setStatus(detail.getStatus());

            detailRepo.save(bd);

            // Update Service đi kèm (Xóa cũ thêm mới)
            bookingHotelServiceRepo.deleteByBookingDetail(bd);
            for (BookingDetailDTO.ServiceDTO service : detail.getServices()) {
                BookingHotelService bhs = new BookingHotelService();
                bhs.setBookingDetail(bd);
                bhs.setService(hotelServiceRepo.findById(service.getHotelServiceId()).get());
                bhs.setQuantity(service.getQuantity());
                bookingHotelServiceRepo.save(bhs);
            }
        }
        return error;
    }
    
    @Transactional
    public void cancelBooking(Long id, boolean isDetail){
        if (isDetail) {
            BookingDetail bd = detailRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng!"));
            bd.setStatus("CANCELLED");
            detailRepo.save(bd);
        } else {
            // Tìm và update toàn bộ phòng thuộc 1 đơn đặt (Booking)
            Booking b = bookingRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt!"));

            List<BookingDetail> details = detailRepo.findAllByBooking(b);
            for (BookingDetail item : details) {
                item.setStatus("CANCELLED");
            }
            detailRepo.saveAll(details);
        }
    }
    
}
