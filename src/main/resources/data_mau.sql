-- 0. TẮT KIỂM TRA KHÓA NGOẠI
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Bảng `room_type`
INSERT IGNORE INTO `room_type` (`room_type_id`, `name`, `price`, `description`) VALUES
(1, 'Single Room', 500000, 'Phòng đơn tiêu chuẩn'),
(2, 'Double Room', 800000, 'Phòng đôi tiện nghi'),
(3, 'Twin Room', 850000, 'Phòng 2 giường đơn'),
(4, 'Suite', 2000000, 'Phòng cao cấp, view biển'),
(5, 'Deluxe', 1200000, 'Phòng sang trọng diện tích lớn'),
(6, 'Family Room', 1500000, 'Phòng cho gia đình 4 người'),
(7, 'VIP', 3000000, 'Phòng dành cho khách VIP'),
(8, 'Standard', 400000, 'Phòng tiêu chuẩn giá rẻ'),
(9, 'Studio', 1100000, 'Phòng có bếp nhỏ'),
(10, 'Penthouse', 5000000, 'Căn hộ tầng thượng');

-- 2. Bảng `room`
INSERT IGNORE INTO `room` (`room_id`, `room_type_id`, `room_number`, `status`) VALUES
(1, 1, '101', 'AVAILABLE'),
(2, 2, '102', 'AVAILABLE'),
(3, 3, '201', 'BOOKED'),
(4, 4, '202', 'OCCUPIED'),
(5, 5, '301', 'CLEANING'),
(6, 6, '401', 'AVAILABLE'),
(7, 7, '501', 'AVAILABLE'),
(8, 8, '601', 'OCCUPIED'),
(9, 9, '701', 'AVAILABLE'),
(10, 10, '103', 'CLEANING');

-- 3. Bảng `users`
INSERT IGNORE INTO `users` (`user_id`, `username`, `password`, `role`, `full_name`) VALUES
(1, 'admin_hung', '123456', 'ADMIN', 'Nguyễn Mạnh Hùng'),
(2, 'recep_lan', '123456', 'RECEPTIONIST', 'Lê Thị Lan'),
(3, 'recep_minh', '123456', 'RECEPTIONIST', 'Trần Quang Minh'),
(4, 'staff_tuan', '123456', 'STAFF', 'Phạm Anh Tuấn'),
(5, 'staff_hoa', '123456', 'STAFF', 'Lê Thanh Hòa'),
(6, 'admin_dung', '123456', 'ADMIN', 'Vũ Tiến Dũng'),
(7, 'recep_vy', '123456', 'RECEPTIONIST', 'Hoàng Thảo Vy'),
(8, 'staff_nam', '123456', 'STAFF', 'Đỗ Thành Nam'),
(9, 'staff_linh', '123456', 'STAFF', 'Ngô Khánh Linh'),
(10, 'recep_an', '123456', 'RECEPTIONIST', 'Bùi Bình An'),
(11, 'alp', '$2a$10$qTfvFKRqOI53lHoTQlHLPuxJM/ZIq8VXy4lVeXBms.o76YBj6bbVe', 'ADMIN', 'Alp Admin');

-- 4. Bảng `customer`
INSERT IGNORE INTO `customer` (`customer_id`, `name`, `phone`, `email`, `id_card`) VALUES
(1, 'Nguyễn Văn A', '0901234567', 'ana@gmail.com', '123456789'),
(2, 'Trần Thị B', '0902345678', 'btran@gmail.com', '234567891'),
(3, 'Lê Văn C', '0903456789', 'cle@gmail.com', '345678912'),
(4, 'Phạm Minh D', '0904567890', 'dpham@gmail.com', '456789123'),
(5, 'Hoàng Anh E', '0905678901', 'ehoang@gmail.com', '567891234'),
(6, 'Đỗ Thu F', '0906789012', 'fdo@gmail.com', '678901234'),
(7, 'Ngô Bảo G', '0907890123', 'gngo@gmail.com', '789012345'),
(8, 'Lý Công H', '0908901234', 'hly@gmail.com', '890123456'),
(9, 'Vũ Hoàng I', '0909012345', 'ivu@gmail.com', '901234567'),
(10, 'Bùi Thị K', '0910123456', 'kbui@gmail.com', '012345678');

-- 5. Bảng `service`
INSERT IGNORE INTO `service` (`service_id`, `name`, `price`) VALUES
(1, 'Laundry', 50000), (2, 'Breakfast', 100000), (3, 'Airport Transfer', 300000),
(4, 'Spa/Massage', 500000), (5, 'Mini Bar', 150000), (6, 'Swimming Pool', 0),
(7, 'Extra Bed', 200000), (8, 'Room Service', 50000), (9, 'Tour Guide', 1000000),
(10, 'Rental Bike', 100000);

-- 6. Bảng `booking`
INSERT IGNORE INTO `booking` (`booking_id`, `customer_id`, `user_id`) VALUES
(1, 1, 2), (2, 2, 2), (3, 3, 3), (4, 4, 4), (5, 5, 5),
(6, 6, 6), (7, 7, 7), (8, 8, 8), (9, 9, 9), (10, 10, 10);

-- 7. Bảng `booking_detail`
INSERT IGNORE INTO `booking_detail` (`booking_detail_id`, `booking_id`, `room_id`, `check_in_date`, `check_out_date`, `status`, `price_at_booking`) VALUES
(1, 1, 1, '2026-03-01 14:00:00', '2026-03-03 12:00:00', 'COMPLETED', 500000),
(2, 2, 2, '2026-03-05 14:00:00', '2026-03-07 12:00:00', 'COMPLETED', 800000),
(3, 3, 3, '2026-04-10 14:00:00', '2026-04-12 12:00:00', 'PENDING', 850000),
(4, 4, 4, '2026-04-01 14:00:00', '2026-04-15 12:00:00', 'CHECKED_IN', 2000000),
(5, 5, 5, '2026-04-20 14:00:00', '2026-04-22 12:00:00', 'PENDING', 1200000),
(6, 6, 7, '2026-04-22 14:00:00', '2026-04-25 12:00:00', 'PENDING', 3000000),
(7, 7, 8, '2026-04-05 14:00:00', '2026-04-10 12:00:00', 'CHECKED_IN', 400000),
(8, 8, 9, '2026-04-25 14:00:00', '2026-04-28 12:00:00', 'PENDING', 1100000),
(9, 9, 10, '2026-04-26 14:00:00', '2026-04-27 12:00:00', 'PENDING', 5000000),
(10, 10, 4, '2026-05-28 14:00:00', '2026-05-30 12:00:00', 'PENDING', 2000000);

-- 8. Bảng `booking_service`
INSERT IGNORE INTO `booking_service` (`booking_detail_id`, `service_id`, `quantity`) VALUES
(1, 1, 2), (2, 1, 2), (4, 3, 1), (7, 7, 2);

-- 9. Bảng `payment`
INSERT IGNORE INTO `payment` (`payment_id`, `booking_id`, `total_amount`, `payment_date`, `status`) VALUES
(1, 1, 1300000, '2026-03-03 09:00:00', 'PAID'),
(2, 2, 1850000, '2026-03-07 09:00:00', 'PAID'),
(3, 4, 2500000, '2026-04-17 09:00:00', 'UNPAID');

-- 10. BẬT LẠI KIỂM TRA KHÓA NGOẠI
SET FOREIGN_KEY_CHECKS = 1;
