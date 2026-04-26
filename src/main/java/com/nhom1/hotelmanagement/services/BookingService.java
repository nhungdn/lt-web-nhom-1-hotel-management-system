package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.BookingDTO;
import com.nhom1.hotelmanagement.dto.BookingDetailDTO;
import com.nhom1.hotelmanagement.entities.Booking;
import com.nhom1.hotelmanagement.entities.BookingDetail;
import com.nhom1.hotelmanagement.entities.BookingHotelService;
import com.nhom1.hotelmanagement.entities.Customer;
import com.nhom1.hotelmanagement.entities.Room;
import com.nhom1.hotelmanagement.entities.RoomType;
import com.nhom1.hotelmanagement.repositories.BookingDetailRepository;
import com.nhom1.hotelmanagement.repositories.BookingRepository;
import com.nhom1.hotelmanagement.repositories.CustomerRepository;
import com.nhom1.hotelmanagement.repositories.RoomRepository;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nhom1.hotelmanagement.repositories.BookingHotelServiceRepository;
import com.nhom1.hotelmanagement.repositories.HotelServiceRepository;
import com.nhom1.hotelmanagement.repositories.RoomTypeRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final int CHECK_IN_HOUR = 14;
    private static final int CHECK_OUT_HOUR = 12;

    @Autowired
    private BookingRepository bookingRepo;
    @Autowired
    private BookingDetailRepository detailRepo;
    @Autowired
    private RoomRepository roomRepo;
    @Autowired
    private RoomTypeRepository roomTypeRepo;
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private BookingHotelServiceRepository bookingHotelServiceRepo;
    @Autowired
    private HotelServiceRepository hotelServiceRepo;

    // public int checkCustomer(String phone, String idCard, String email) {
    // if (customerRepo.existsByPhone(phone)) return 1;
    // if (customerRepo.existsByEmail(email)) return 2;
    // if (customerRepo.existsByIdCard(idCard)) return 3;
    // return 0;
    // }

    public List<BookingDetailDTO> getAllBooking() {
        return bookingRepo.findAll().stream()
                .sorted(Comparator.comparing(Booking::getBookingId).reversed())
                .map(this::convertToDto) // Tách logic convert ra một hàm riêng
                .collect(Collectors.toList());
    }

    public List<BookingDetailDTO> getBookingsByMonth(int month, int year) {
        // Database chỉ trả về các bản ghi khớp với tháng/năm
        List<Booking> entities = bookingRepo.findByMonthAndYear(month, year);

        // Chuyển đổi Entity sang DTO
        return entities.stream()
                .map(this::convertToDto)
                .toList();
    }

    private BookingDetailDTO convertToDto(Booking b) {
        BookingDetailDTO dto = new BookingDetailDTO();
        dto.setBookingId(b.getBookingId());

        Optional.ofNullable(b.getCustomer()).ifPresent(c -> {
            dto.setCustomerName(c.getName());
            dto.setCustomerEmail(c.getEmail());
            dto.setCustomerIDCard(c.getIdCard());
        });

        // 1. Map danh sách Details
        List<BookingDetailDTO.DetailDTO> details = b.getBookingDetails().stream()
                .map(bd -> {
                    BookingDetailDTO.DetailDTO dDto = new BookingDetailDTO.DetailDTO();
                    dDto.setBookingDetailId(bd.getBookingDetailId());
                    dDto.setRoomNumber(bd.getRoom() != null ? bd.getRoom().getRoomNumber() : "N/A");
                    dDto.setRoomId(bd.getRoom() != null ? bd.getRoom().getRoomId() : null);
                    dDto.setStatus(bd.getStatus());
                    dDto.setCheckIn(bd.getCheckInDate().toString());
                    dDto.setCheckOut(bd.getCheckOutDate().toString());

                    // Lưu giá phòng tại thời điểm đặt
                    // BigDecimal roomPrice = bd.getPriceAtBooking() != null ?
                    // bd.getPriceAtBooking() : BigDecimal.ZERO;
                    // dDto.setPrice(roomPrice); // Nếu DTO của bạn có trường này

                    if (bd.getBookingHotelServices() != null) {
                        List<BookingDetailDTO.ServiceDTO> sDtos = bd.getBookingHotelServices().stream()
                                .filter(bhs -> bhs.getService() != null)
                                .map(bhs -> {
                                    BookingDetailDTO.ServiceDTO sDto = new BookingDetailDTO.ServiceDTO();
                                    sDto.setHotelServiceId(bhs.getService().getServiceId());
                                    sDto.setServiceName(bhs.getService().getName());
                                    sDto.setQuantity(bhs.getQuantity());
                                    sDto.setPrice(bhs.getService().getPrice());
                                    sDto.setAddedAt(formatDateTime(bhs.getAddedAt()));
                                    return sDto;
                                }).collect(Collectors.toList());
                        dDto.setServices(sDtos);
                    }
                    return dDto;
                }).collect(Collectors.toList());

        dto.setDetails(details);

        // Tính tổng tiền
        BigDecimal total = b.getBookingDetails().stream()
                .map(bd -> {
                    // Tiền phòng
                    BigDecimal rPrice = bd.getPriceAtBooking() != null ? bd.getPriceAtBooking() : BigDecimal.ZERO;

                    // Tiền dịch vụ = Sum(price * quantity)
                    BigDecimal sPrice = bd.getBookingHotelServices().stream()
                            .filter(s -> s.getService() != null)
                            .map(s -> s.getService().getPrice().multiply(BigDecimal.valueOf(s.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return rPrice.add(sPrice);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setTotalAmount(total);
        return dto;
    }

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
            LocalDateTime startTime = toCheckInDateTime(item.getCheckIn());
            LocalDateTime endTime = toCheckOutDateTime(item.getCheckOut());

            List<Room> availRooms = roomRepo.findAvailableRoomByType(item.getRoomTypeId(), startTime, endTime);

            if (availRooms.size() < item.getQuantity()) {
                RoomType rt = roomTypeRepo.findById(item.getRoomTypeId()).orElse(null);
                errors.add(
                        "Loại phòng " + (rt != null ? rt.getName() : "ID: " + item.getRoomTypeId()) + " không đủ chỗ.");
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

            LocalDateTime startTime = toCheckInDateTime(item.getCheckIn());
            LocalDateTime endTime = toCheckOutDateTime(item.getCheckOut());

            if (roomsForThisType == null || roomsForThisType.size() < item.getQuantity()) {
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
                saveServiceItems(savedDetail, item.getServiceItems());
            }
        }

        return errors;

    }

    @Transactional
    public List<String> editBooking(BookingDetailDTO request) {
        System.out.println("Service nhận request edit: ");
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
            saveEditedServices(bd, detail.getServices());
        }
        return error;
    }

    private void saveServiceItems(BookingDetail detail, List<BookingDTO.ServiceItem> serviceItems) {
        if (serviceItems == null || serviceItems.isEmpty()) {
            return;
        }

        for (BookingDTO.ServiceItem serviceItem : serviceItems) {
            hotelServiceRepo.findById(serviceItem.getServiceId()).ifPresent(hs -> {
                BookingHotelService bhs = new BookingHotelService();
                bhs.setBookingDetail(detail);
                bhs.setService(hs);
                bhs.setQuantity(serviceItem.getQuantity());
                bhs.setAddedAt(LocalDateTime.now());
                bookingHotelServiceRepo.save(bhs);
            });
        }
    }

    private void saveEditedServices(BookingDetail detail, List<BookingDetailDTO.ServiceDTO> services) {
        if (services == null || services.isEmpty()) {
            return;
        }

        for (BookingDetailDTO.ServiceDTO service : services) {
            hotelServiceRepo.findById(service.getHotelServiceId()).ifPresent(hs -> {
                BookingHotelService bhs = new BookingHotelService();
                bhs.setBookingDetail(detail);
                bhs.setService(hs);
                bhs.setQuantity(service.getQuantity());
                bhs.setAddedAt(LocalDateTime.now());
                bookingHotelServiceRepo.save(bhs);
            });
        }
    }

    private LocalDateTime toCheckInDateTime(String checkInDate) {
        return LocalDate.parse(checkInDate).atTime(CHECK_IN_HOUR, 0);
    }

    private LocalDateTime toCheckOutDateTime(String checkOutDate) {
        return LocalDate.parse(checkOutDate).atTime(CHECK_OUT_HOUR, 0);
    }

    @Transactional
    public void changeStatus(Long id, String status) {
        BookingDetail bd = detailRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng!"));
        bd.setStatus(BookingDetail.Status.valueOf(status));
        detailRepo.save(bd);
    }

    @Transactional
    public void cancelBooking(Long id, boolean isDetail) {
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

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }

}
