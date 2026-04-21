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
import java.util.HashMap;
import java.util.Map;

@Service
public class BookingService {
    @Autowired private BookingRepository bookingRepo;
    @Autowired private BookingDetailRepository detailRepo;
    @Autowired private RoomRepository roomRepo;
    @Autowired private RoomTypeRepository roomTypeRepo;
    @Autowired private CustomerRepository customerRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private BookingHotelServiceRepository bookingHotelServiceRepo;
    @Autowired private HotelServiceRepository hotelServiceRepo;
    
//    public int checkCustomer(String phone, String idCard, String email) {
//        if (customerRepo.existsByPhone(phone)) return 1;
//        if (customerRepo.existsByEmail(email)) return 2;
//        if (customerRepo.existsByIdCard(idCard)) return 3;
//        return 0;
//    }

    @Transactional
    public List<String> createBooking(BookingDTO.MultiSubmitRequest request, HttpSession session) {
        System.out.println("service: " + request);

        // Xử lý Customer
        Customer customer = customerRepo.findByIdCard(request.getCustomerIdCard());
        if (customer == null) {
            customer = new Customer();
        }
        customer.setName(request.getCustomerName());
        customer.setPhone(request.getCustomerPhone());
        customer.setIdCard(request.getCustomerIdCard());
        customer.setEmail(request.getCustomerEmail());
        customer = customerRepo.save(customer);

        List<String> errors = new ArrayList<>();
        // Dùng Map để lưu lại danh sách phòng trống đã tìm thấy ở bước kiểm tra
        // Key là roomTypeId, Value là danh sách các phòng trống (Room)
        Map<Long, List<Room>> availRoomsMap = new HashMap<>();

        // KIỂM TRA & CACHE PHÒNG
        for (BookingDTO.BookingItem item : request.getBookingItems()) {
            // Đồng nhất khung giờ check-in/out
            LocalDateTime startTime = LocalDate.parse(item.getCheckIn()).atTime(14, 0);
            LocalDateTime endTime = LocalDate.parse(item.getCheckOut()).atTime(12, 0);

            List<Room> availRooms = roomRepo.findAvailableRoomByType(item.getRoomTypeId(), startTime, endTime);

            if (availRooms.size() < item.getQuantity()) {
                RoomType rt = roomTypeRepo.findById(item.getRoomTypeId()).orElse(null);
                errors.add("Loại phòng " + (rt != null ? rt.getName() : "ID: " + item.getRoomTypeId()) + " không đủ chỗ.");
            } else {
                // Nếu đủ, cho vào Map
                availRoomsMap.put(item.getRoomTypeId(), availRooms);
            }
        }

        // Nếu có bất kỳ lỗi nào, Transaction sẽ Rollback (không lưu Customer/Booking)
        if (!errors.isEmpty()) {
            return errors;
        }

        // LƯU BOOKING
        Booking book = new Booking();
        book.setCustomer(customer);
        bookingRepo.save(book);

        // LƯU DETAILS & SERVICES
        for (BookingDTO.BookingItem item : request.getBookingItems()) {
            // Lấy danh sách phòng
            List<Room> roomsForThisType = availRoomsMap.get(item.getRoomTypeId());

            LocalDateTime startTime = LocalDate.parse(item.getCheckIn()).atTime(14, 0);
            LocalDateTime endTime = LocalDate.parse(item.getCheckOut()).atTime(12, 0);

            if (availRoomsMap.size() < item.getQuantity()) {
                throw new RuntimeException("Phòng không đủ");
            }

            for (int i = 0; i < item.getQuantity(); i++) {
                Room room = roomsForThisType.get(i); // Lấy phòng thứ i trong danh sách trống

                BookingDetail detail = new BookingDetail();
                detail.setRoom(room);
                detail.setBooking(book);
                detail.setCheckInDate(startTime);
                detail.setCheckOutDate(endTime);
                detail.setStatus(BookingDetail.Status.PENDING);
                detail.setPriceAtBooking(room.getRoomType().getPrice());
                
                BookingDetail savedDetail = detailRepo.save(detail);

                // Lưu Dịch vụ
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

        return errors;

    }

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
            bd.setStatus(BookingDetail.Status.CANCELED);
            detailRepo.save(bd);
        } else {
            // Tìm và update toàn bộ phòng thuộc 1 đơn đặt (Booking)
            Booking b = bookingRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt!"));

            List<BookingDetail> details = detailRepo.findAllByBooking(b);
            for (BookingDetail item : details) {
                item.setStatus(BookingDetail.Status.CANCELED);
            }
            detailRepo.saveAll(details);
        }
    }
    
}
