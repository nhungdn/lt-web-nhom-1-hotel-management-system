-- 1. Chèn dữ liệu vào bảng RoomType
INSERT INTO room_type (name, price, description) VALUES
('Single Room', 500000, 'Phòng đơn tiêu chuẩn'),
('Double Room', 800000, 'Phòng đôi tiện nghi'),
('Twin Room', 850000, 'Phòng 2 giường đơn'),
('Suite', 2000000, 'Phòng cao cấp, view biển'),
('Deluxe', 1200000, 'Phòng sang trọng diện tích lớn'),
('Family Room', 1500000, 'Phòng cho gia đình 4 người'),
('VIP', 3000000, 'Phòng dành cho khách VIP'),
('Standard', 400000, 'Phòng tiêu chuẩn giá rẻ'),
('Studio', 1100000, 'Phòng có bếp nhỏ'),
('Penthouse', 5000000, 'Căn hộ tầng thượng');

-- 2. Chèn dữ liệu vào bảng Room
INSERT INTO room (room_type_id, room_number, status) VALUES
(1, '101', 'AVAILABLE'),
(2, '102', 'AVAILABLE'),
(3, '201', 'BOOKED'),
(4, '202', 'OCCUPIED'),
(5, '301', 'CLEANING'),
(6, '401', 'AVAILABLE'),
(7, '501', 'AVAILABLE'),
(8, '601', 'OCCUPIED'),
(9, '701', 'AVAILABLE'),
(10, '103', 'CLEANING');

-- 3. Chèn dữ liệu vào bảng User
INSERT INTO users (username, password, role) VALUES
('admin_hung', '123456', 'ADMIN'),
('recep_lan', '123456', 'RECEPTIONIST'),
('recep_minh', '123456', 'RECEPTIONIST'),
('staff_tuan', '123456', 'STAFF'),
('staff_hoa', '123456', 'STAFF'),
('admin_dung', '123456', 'ADMIN'),
('recep_vy', '123456', 'RECEPTIONIST'),
('staff_nam', '123456', 'STAFF'),
('staff_linh', '123456', 'STAFF'),
('recep_an', '123456', 'RECEPTIONIST');

-- 4. Chèn dữ liệu vào bảng Customer
INSERT INTO customer (name, phone, email, id_card) VALUES
('Nguyễn Văn A', '0901234567', 'ana@gmail.com', '123456789'),
('Trần Thị B', '0902345678', 'btran@gmail.com', '234567891'),
('Lê Văn C', '0903456789', 'cle@gmail.com', '345678912'),
('Phạm Minh D', '0904567890', 'dpham@gmail.com', '456789123'),
('Hoàng Anh E', '0905678901', 'ehoang@gmail.com', '567891234'),
('Đỗ Thu F', '0906789012', 'fdo@gmail.com', '678901234'),
('Ngô Bảo G', '0907890123', 'gngo@gmail.com', '789012345'),
('Lý Công H', '0908901234', 'hly@gmail.com', '890123456'),
('Vũ Hoàng I', '0909012345', 'ivu@gmail.com', '901234567'),
('Bùi Thị K', '0910123456', 'kbui@gmail.com', '012345678');

-- 5. Chèn dữ liệu vào bảng Service
INSERT INTO service (name, price) VALUES
('Laundry', 50000),
('Breakfast', 100000),
('Airport Transfer', 300000),
('Spa/Massage', 500000),
('Mini Bar', 150000),
('Swimming Pool', 0),
('Extra Bed', 200000),
('Room Service', 50000),
('Tour Guide', 1000000),
('Rental Bike', 100000);

-- 6. Chèn dữ liệu vào bảng Booking
INSERT INTO booking (customer_id, user_id) VALUES
(1, 2), (2, 2), (3, 3), (4, 4), (5, 5),
(6, 6), (7, 7), (8, 8), (9, 9), (10, 10);

-- 7. Chèn dữ liệu vào bảng BookingDetail
INSERT INTO booking_detail (booking_id, room_id, check_in_date, check_out_date, status, price_at_booking) VALUES
(1, 1, '2024-03-01 14:00:00', '2024-03-03 12:00:00', 'COMPLETED', 500000),
(2, 2, '2024-03-05 14:00:00', '2024-03-07 12:00:00', 'COMPLETED', 800000),
(3, 4, '2024-03-10 14:00:00', '2024-03-12 12:00:00', 'CANCELLED', 500000),
(4, 5, '2024-03-15 14:00:00', '2024-03-17 12:00:00', 'CHECKED_IN', 800000),
(5, 6, '2024-03-20 14:00:00', '2024-03-22 12:00:00', 'PENDING', 500000),
(6, 7, '2024-03-22 14:00:00', '2024-03-25 12:00:00', 'PENDING', 500000),
(7, 8, '2024-03-24 14:00:00', '2024-03-26 12:00:00', 'CHECKED_IN', 2000000),
(8, 9, '2024-03-25 14:00:00', '2024-03-28 12:00:00', 'PENDING', 1500000),
(9, 10, '2024-03-26 14:00:00', '2024-03-27 12:00:00', 'PENDING', 1100000),
(10, 4, '2024-03-28 14:00:00', '2024-03-30 12:00:00', 'PENDING', 1200000);

-- 8. Chèn dữ liệu vào bảng BookingService
INSERT INTO booking_service (booking_detail_id, service_id, quantity) VALUES
(1, 1, 2),
(2, 1, 2),
(3, 2, 3),
(4, 3, 1),
(5, 4, 1),
(6, 3, 1),
(7, 7, 2),
(8, 10, 5),
(9, 3, 8),
(10, 1, 10);

-- 9. Chèn dữ liệu vào bảng Payment
INSERT INTO payment (booking_id, total_amount, payment_date, status) VALUES
(1, 1300000, '2024-03-03', 'PAID'),
(2, 1850000, '2024-03-07', 'PAID'),
(3, 0, '2024-03-10', 'REFUNDED'),
(4, 2500000, '2024-03-17', 'UNPAID'),
(5, 4500000, '2024-03-22', 'UNPAID');

-- 10. Chèn dữ liệu vào bảng RoomImage
INSERT INTO room_image (room_id, image_url, description) VALUES
(1, 'img/room1_1.jpg', 'View phòng 101'),
(2, 'img/room1_2.jpg', 'Toà wc phòng 101'),
(3, 'img/room2.jpg', 'View phòng 102'),
(4, 'img/room3.jpg', 'Giường đôi phòng 201'),
(5, 'img/room4.jpg', 'Ban công phòng 202');