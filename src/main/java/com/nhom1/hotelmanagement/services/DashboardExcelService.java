package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.BookingDetailDashboardDTO;
import com.nhom1.hotelmanagement.entities.Payment;
import com.nhom1.hotelmanagement.entities.Room;
import com.nhom1.hotelmanagement.entities.User;
import com.nhom1.hotelmanagement.entities.HotelService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class DashboardExcelService {

        @Autowired
        private DashboardService dashboardService;
        @Autowired
        private RoomService roomService;
        @Autowired
        private HotelServiceService hotelServiceService;
        @Autowired
        private UserService userService;

        private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // ── Màu sắc ──────────────────────────────────────────────────────────
        private static final String BG_DARK = "1A2B1F";
        private static final String BG_MID = "243320";
        private static final String BG_HEADER = "0E1A12";
        private static final String BG_TBL = "162A1C";
        private static final String C_GREEN = "4ADE80";
        private static final String C_GOLD = "F0B429";
        private static final String C_BLUE = "60A5FA";
        private static final String C_ORANGE = "FB923C";
        private static final String C_RED = "F87171";
        private static final String C_WHITE = "E8F0EB";
        private static final String C_MUTED = "7A9A82";

        public byte[] generateReport() throws IOException {
                try (XSSFWorkbook wb = new XSSFWorkbook();
                                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                        // ── Lấy dữ liệu thật từ DB ──────────────────────────────────
                        long totalBookings = dashboardService.getTotalBookings();
                        BigDecimal totalRevenue = dashboardService.getTotalRevenue();
                        long available = dashboardService.countRoomByStatus("AVAILABLE");
                        long occupied = dashboardService.countRoomByStatus("OCCUPIED");
                        long cleaning = dashboardService.countRoomByStatus("CLEANING");
                        long booked = dashboardService.countRoomByStatus("BOOKED");
                        long pending = dashboardService.countBookingByStatus("PENDING");
                        long checkedIn = dashboardService.countBookingByStatus("CHECKED_IN");
                        long unpaid = dashboardService.countUnpaidPayments();

                        List<BookingDetailDashboardDTO> recentBookings = dashboardService.getRecentBookings(50);
                        List<Payment> payments = dashboardService.getRecentPayments(50);
                        List<Room> rooms = roomService.listAll();
                        List<HotelService> services = hotelServiceService.listAll();
                        List<User> users = userService.getAllUsers();

                        buildSheetTongQuan(wb, totalBookings, totalRevenue,
                                        available, occupied, cleaning, booked,
                                        pending, checkedIn, unpaid, recentBookings);

                        buildSheetPhong(wb, rooms, available, occupied, cleaning, booked);

                        buildSheetThanhToan(wb, payments);

                        buildSheetDichVu(wb, services);

                        buildSheetNhanVien(wb, users);

                        wb.write(out);
                        return out.toByteArray();
                }
        }

        // ════════════════════════════════════════════════════════════════════
        // SHEET 1 – TỔNG QUAN
        // ════════════════════════════════════════════════════════════════════
        private void buildSheetTongQuan(XSSFWorkbook wb,
                        long totalBookings, BigDecimal totalRevenue,
                        long available, long occupied, long cleaning, long booked,
                        long pending, long checkedIn, long unpaid,
                        List<BookingDetailDashboardDTO> bookings) {

                XSSFSheet ws = wb.createSheet("Tổng Quan");
                ws.setDisplayGridlines(false);

                // Banner
                setRowHeight(ws, 0, 12);
                setRowHeight(ws, 1, 40);
                setRowHeight(ws, 2, 22);
                setRowHeight(ws, 3, 12);

                mergeRow(ws, wb, 1, 0, 11,
                                "🏨  BÁO CÁO TỔNG QUAN KHÁCH SẠN",
                                boldFont(wb, C_GREEN, 18), BG_HEADER, HorizontalAlignment.LEFT);

                mergeRow(ws, wb, 2, 0, 11,
                                "Dữ liệu thực từ cơ sở dữ liệu  •  Xuất lúc: "
                                                + java.time.LocalDateTime.now().format(FMT),
                                italicFont(wb, C_MUTED, 10), BG_HEADER, HorizontalAlignment.LEFT);

                // Stat cards (row 4–8)
                for (int r = 4; r <= 8; r++)
                        setRowHeight(ws, r, r == 5 ? 34 : 18);

                buildStatCard(ws, wb, 4, 0, "📋  TỔNG ĐẶT PHÒNG",
                                String.valueOf(totalBookings),
                                "Tổng số booking trong hệ thống", C_GREEN);

                buildStatCard(ws, wb, 4, 3, "💰  DOANH THU (PAID)",
                                formatVnd(totalRevenue),
                                "Từ bảng payment – status PAID", C_GOLD);

                buildStatCard(ws, wb, 4, 6, "🏨  PHÒNG TRỐNG",
                                String.valueOf(available),
                                "AVAILABLE / " + (available + occupied + cleaning + booked) + " phòng tổng", C_BLUE);

                buildStatCard(ws, wb, 4, 9, "⏳  CHỜ XÁC NHẬN",
                                String.valueOf(pending),
                                checkedIn + " CHECKED_IN  •  " + unpaid + " chưa thanh toán", C_ORANGE);

                // Booking table
                setRowHeight(ws, 9, 20);
                mergeRow(ws, wb, 9, 0, 11,
                                "📋  ĐẶT PHÒNG GẦN ĐÂY",
                                boldFont(wb, C_GREEN, 12), BG_HEADER, HorizontalAlignment.LEFT);

                String[] headers = { "#", "Khách hàng", "SĐT", "Phòng", "Loại phòng",
                                "Check-in", "Check-out", "Trạng thái", "Giá/đêm (₫)" };
                XSSFRow hRow = ws.createRow(10);
                setRowHeight(ws, 10, 22);
                for (int i = 0; i < headers.length; i++) {
                        XSSFCell c = hRow.createCell(i);
                        c.setCellValue(headers[i]);
                        c.setCellStyle(headerCellStyle(wb));
                }

                String[] statusLabels = { "PENDING", "CHECKED_IN", "COMPLETED", "CANCELLED" };
                String[] statusVi = { "Chờ xác nhận", "Đang ở", "Hoàn thành", "Đã hủy" };

                for (int i = 0; i < bookings.size(); i++) {
                        BookingDetailDashboardDTO bd = bookings.get(i);
                        XSSFRow row = ws.createRow(11 + i);
                        setRowHeight(ws, 11 + i, 20);
                        String rowBg = i % 2 == 0 ? BG_DARK : BG_MID;

                        setCellStr(row, 0, String.valueOf(i + 1), normalFont(wb, C_MUTED, 10), rowBg);
                        setCellStr(row, 1, nvl(bd.getCustomerName()), boldFont(wb, C_WHITE, 10), rowBg);
                        setCellStr(row, 2, nvl(bd.getCustomerPhone()), normalFont(wb, C_WHITE, 10), rowBg);
                        setCellStr(row, 3, nvl(bd.getRoomNumber()), boldFont(wb, C_WHITE, 10), rowBg);
                        setCellStr(row, 4, nvl(bd.getRoomTypeName()), normalFont(wb, C_WHITE, 10), rowBg);
                        setCellStr(row, 5, bd.getCheckInDate() != null ? bd.getCheckInDate().format(DATE) : "",
                                        normalFont(wb, C_WHITE, 10), rowBg);
                        setCellStr(row, 6, bd.getCheckOutDate() != null ? bd.getCheckOutDate().format(DATE) : "",
                                        normalFont(wb, C_WHITE, 10), rowBg);

                        // Status badge
                        String st = nvl(bd.getStatus().toString());
                        String stVi = st;
                        for (int k = 0; k < statusLabels.length; k++) {
                                if (statusLabels[k].equals(st)) {
                                        stVi = statusVi[k];
                                        break;
                                }
                        }
                        Map<String, String[]> stMap = Map.of(
                                        "PENDING", new String[] { C_GOLD, "3D2E0A" },
                                        "CHECKED_IN", new String[] { C_BLUE, "0F2744" },
                                        "COMPLETED", new String[] { C_GREEN, "0F2E1A" },
                                        "CANCELLED", new String[] { C_RED, "3D1212" });
                        String[] stEntry = stMap.getOrDefault(st, new String[] { C_WHITE, rowBg });
                        setCellStr(row, 7, stVi, boldFont(wb, stEntry[0], 10), stEntry[1]);

                        // Giá (lấy từ roomType price)
                        BigDecimal price = getPrice(bd);
                        XSSFCell priceCell = row.createCell(8);
                        if (price != null) {
                                priceCell.setCellValue(price.doubleValue());
                                priceCell.setCellStyle(currencyStyle(wb, rowBg));
                        } else {
                                setCellStr(row, 8, "—", normalFont(wb, C_MUTED, 10), rowBg);
                        }
                }

                // Col widths
                int[] widths = { 5, 20, 14, 8, 16, 12, 12, 16, 18, 12, 12, 12 };
                for (int i = 0; i < widths.length; i++)
                        ws.setColumnWidth(i, widths[i] * 256);
        }

        // ════════════════════════════════════════════════════════════════════
        // SHEET 2 – TÌNH TRẠNG PHÒNG
        // ════════════════════════════════════════════════════════════════════
        private void buildSheetPhong(XSSFWorkbook wb, List<Room> rooms,
                        long available, long occupied, long cleaning, long booked) {

                XSSFSheet ws = wb.createSheet("Tình Trạng Phòng");
                ws.setDisplayGridlines(false);

                setRowHeight(ws, 0, 36);
                mergeRow(ws, wb, 0, 0, 5,
                                "🏨  TÌNH TRẠNG PHÒNG – REAL TIME",
                                boldFont(wb, C_GREEN, 14), BG_HEADER, HorizontalAlignment.LEFT);

                // Headers
                String[] headers = { "Phòng", "Loại phòng", "Trạng thái", "Giá/đêm (₫)", "Mô tả" };
                XSSFRow hRow = ws.createRow(1);
                setRowHeight(ws, 1, 22);
                for (int i = 0; i < headers.length; i++) {
                        XSSFCell c = hRow.createCell(i);
                        c.setCellValue(headers[i]);
                        c.setCellStyle(headerCellStyle(wb));
                }

                Map<String, String[]> stMap = Map.of(
                                "AVAILABLE", new String[] { "Trống", C_GREEN, "0F2E1A" },
                                "OCCUPIED", new String[] { "Đang ở", C_BLUE, "0F2744" },
                                "BOOKED", new String[] { "Đã đặt", C_GOLD, "3D2E0A" },
                                "CLEANING", new String[] { "Dọn dẹp", C_ORANGE, "3D1F0A" });

                for (int i = 0; i < rooms.size(); i++) {
                        Room r = rooms.get(i);
                        XSSFRow row = ws.createRow(2 + i);
                        setRowHeight(ws, 2 + i, 20);
                        String rowBg = i % 2 == 0 ? BG_DARK : BG_MID;

                        String stRaw = r.getStatus() != null ? r.getStatus().name() : "";
                        String[] stEntry = stMap.getOrDefault(stRaw, new String[] { stRaw, C_WHITE, rowBg });

                        setCellStr(row, 0, nvl(r.getRoomNumber()),
                                        boldFont(wb, C_WHITE, 10), rowBg);
                        setCellStr(row, 1,
                                        r.getRoomType() != null ? r.getRoomType().getName() : "—",
                                        normalFont(wb, C_WHITE, 10), rowBg);
                        setCellStr(row, 2, stEntry[0],
                                        boldFont(wb, stEntry[1], 10), stEntry[2]);

                        BigDecimal price = r.getRoomType() != null ? r.getRoomType().getPrice() : null;
                        if (price != null) {
                                XSSFCell pc = row.createCell(3);
                                pc.setCellValue(price.doubleValue());
                                pc.setCellStyle(currencyStyle(wb, rowBg));
                        } else {
                                setCellStr(row, 3, "—", normalFont(wb, C_MUTED, 10), rowBg);
                        }

                        setCellStr(row, 4,
                                        r.getRoomType() != null ? nvl(r.getRoomType().getDescription()) : "",
                                        normalFont(wb, C_MUTED, 10), rowBg);
                }

                // Summary
                int sr = rooms.size() + 3;
                setRowHeight(ws, sr, 20);
                mergeRow(ws, wb, sr, 0, 5,
                                "📊  THỐNG KÊ",
                                boldFont(wb, C_GREEN, 11), BG_HEADER, HorizontalAlignment.LEFT);

                Object[][] summary = {
                                { "Tổng số phòng", (long) rooms.size(), C_WHITE },
                                { "Phòng trống", available, C_GREEN },
                                { "Đang ở", occupied, C_BLUE },
                                { "Đã đặt", booked, C_GOLD },
                                { "Dọn dẹp", cleaning, C_ORANGE },
                };
                for (int i = 0; i < summary.length; i++) {
                        int r = sr + 1 + i;
                        setRowHeight(ws, r, 18);
                        XSSFRow row = ws.createRow(r);
                        setCellStr(row, 0, (String) summary[i][0],
                                        normalFont(wb, C_MUTED, 10), BG_MID);
                        XSSFCell vc = row.createCell(1);
                        vc.setCellValue(((Number) summary[i][1]).longValue());
                        vc.setCellStyle(numStyle(wb, (String) summary[i][2], BG_MID));
                        for (int c = 2; c <= 5; c++)
                                setCellStr(row, c, "", normalFont(wb, C_WHITE, 10), BG_MID);
                }

                int[] widths = { 10, 20, 14, 18, 30 };
                for (int i = 0; i < widths.length; i++)
                        ws.setColumnWidth(i, widths[i] * 256);
        }

        // ════════════════════════════════════════════════════════════════════
        // SHEET 3 – THANH TOÁN
        // ════════════════════════════════════════════════════════════════════
        private void buildSheetThanhToan(XSSFWorkbook wb, List<Payment> payments) {
                XSSFSheet ws = wb.createSheet("Thanh Toán");
                ws.setDisplayGridlines(false);

                setRowHeight(ws, 0, 36);
                mergeRow(ws, wb, 0, 0, 5,
                                "💰  BÁO CÁO THANH TOÁN",
                                boldFont(wb, C_GOLD, 14), BG_HEADER, HorizontalAlignment.LEFT);

                // Header
                String[] headers = { "Booking #", "Khách hàng", "Ngày thanh toán",
                                "Tổng tiền (₫)", "Trạng thái" };
                XSSFRow hRow = ws.createRow(1);
                setRowHeight(ws, 1, 22);
                for (int i = 0; i < headers.length; i++) {
                        XSSFCell c = hRow.createCell(i);
                        c.setCellValue(headers[i]);
                        c.setCellStyle(headerCellStyle(wb));
                }

                BigDecimal paidTotal = BigDecimal.ZERO;
                BigDecimal unpaidTotal = BigDecimal.ZERO;

                for (int i = 0; i < payments.size(); i++) {
                        Payment p = payments.get(i);
                        XSSFRow row = ws.createRow(2 + i);
                        setRowHeight(ws, 2 + i, 20);
                        String rowBg = i % 2 == 0 ? BG_DARK : BG_MID;

                        String bid = (p.getBookingDetail() != null && p.getBookingDetail().getBooking() != null)
                                        ? "#" + p.getBookingDetail().getBooking().getBookingId()
                                        : "—";
                        String cus = (p.getBookingDetail() != null
                                        && p.getBookingDetail().getBooking() != null
                                        && p.getBookingDetail().getBooking().getCustomer() != null)
                                                        ? p.getBookingDetail().getBooking().getCustomer().getName()
                                                        : "—";
                        String date = p.getPaymentDate() != null
                                        ? p.getPaymentDate().format(FMT)
                                        : "—";
                        boolean isPaid = "PAID".equals(p.getStatus());

                        setCellStr(row, 0, bid, boldFont(wb, C_WHITE, 10), rowBg);
                        setCellStr(row, 1, cus, normalFont(wb, C_WHITE, 10), rowBg);
                        setCellStr(row, 2, date, normalFont(wb, C_WHITE, 10), rowBg);

                        if (p.getTotalAmount() != null) {
                                XSSFCell pc = row.createCell(3);
                                pc.setCellValue(p.getTotalAmount().doubleValue());
                                pc.setCellStyle(currencyStyle(wb, rowBg));
                                if (isPaid)
                                        paidTotal = paidTotal.add(p.getTotalAmount());
                                else
                                        unpaidTotal = unpaidTotal.add(p.getTotalAmount());
                        } else {
                                setCellStr(row, 3, "—", normalFont(wb, C_MUTED, 10), rowBg);
                        }

                        String[] stEntry = isPaid
                                        ? new String[] { "PAID", C_GREEN, "0F2E1A" }
                                        : new String[] { "UNPAID", C_RED, "3D1212" };
                        setCellStr(row, 4, stEntry[0], boldFont(wb, stEntry[1], 10), stEntry[2]);
                }

                // Tổng kết
                int sr = payments.size() + 3;
                setRowHeight(ws, sr, 20);
                mergeRow(ws, wb, sr, 0, 4,
                                "📊  TỔNG KẾT THANH TOÁN",
                                boldFont(wb, C_GOLD, 11), BG_HEADER, HorizontalAlignment.LEFT);

                Object[][] summary = {
                                { "Tổng đã thu (PAID)", paidTotal, C_GREEN },
                                { "Tổng chưa thu (UNPAID)", unpaidTotal, C_RED },
                                { "Tổng cộng", paidTotal.add(unpaidTotal), C_WHITE },
                };
                for (int i = 0; i < summary.length; i++) {
                        int r = sr + 1 + i;
                        setRowHeight(ws, r, 20);
                        XSSFRow row = ws.createRow(r);
                        setCellStr(row, 0, (String) summary[i][0],
                                        boldFont(wb, C_MUTED, 10), BG_MID);
                        XSSFCell vc = row.createCell(1);
                        vc.setCellValue(((BigDecimal) summary[i][1]).doubleValue());
                        vc.setCellStyle(currencyStyle(wb, BG_MID, (String) summary[i][2]));
                        for (int c = 2; c <= 4; c++)
                                setCellStr(row, c, "", normalFont(wb, C_WHITE, 10), BG_MID);
                }

                int[] widths = { 12, 22, 22, 20, 14 };
                for (int i = 0; i < widths.length; i++)
                        ws.setColumnWidth(i, widths[i] * 256);
        }

        // ════════════════════════════════════════════════════════════════════
        // SHEET 4 – DỊCH VỤ
        // ════════════════════════════════════════════════════════════════════
        private void buildSheetDichVu(XSSFWorkbook wb, List<HotelService> services) {
                XSSFSheet ws = wb.createSheet("Dịch Vụ");
                ws.setDisplayGridlines(false);

                setRowHeight(ws, 0, 36);
                mergeRow(ws, wb, 0, 0, 3,
                                "🛎  DỊCH VỤ KHÁCH SẠN",
                                boldFont(wb, C_GOLD, 14), BG_HEADER, HorizontalAlignment.LEFT);

                String[] headers = { "#", "Tên dịch vụ", "Giá (₫)", "Ghi chú" };
                XSSFRow hRow = ws.createRow(1);
                setRowHeight(ws, 1, 22);
                for (int i = 0; i < headers.length; i++) {
                        XSSFCell c = hRow.createCell(i);
                        c.setCellValue(headers[i]);
                        c.setCellStyle(headerCellStyle(wb));
                }

                for (int i = 0; i < services.size(); i++) {
                        HotelService s = services.get(i);
                        XSSFRow row = ws.createRow(2 + i);
                        setRowHeight(ws, 2 + i, 20);
                        String rowBg = i % 2 == 0 ? BG_DARK : BG_MID;

                        setCellStr(row, 0, String.valueOf(i + 1),
                                        normalFont(wb, C_MUTED, 10), rowBg);
                        setCellStr(row, 1, nvl(s.getName()),
                                        normalFont(wb, C_WHITE, 10), rowBg);

                        boolean free = s.getPrice() == null
                                        || s.getPrice().compareTo(BigDecimal.ZERO) == 0;
                        if (free) {
                                setCellStr(row, 2, "Miễn phí",
                                                boldFont(wb, C_GREEN, 10), rowBg);
                        } else {
                                XSSFCell pc = row.createCell(2);
                                pc.setCellValue(s.getPrice().doubleValue());
                                pc.setCellStyle(currencyStyle(wb, rowBg));
                        }
                        setCellStr(row, 3, "", normalFont(wb, C_MUTED, 10), rowBg);
                }

                // Tổng số dịch vụ
                int sr = services.size() + 3;
                setRowHeight(ws, sr, 20);
                mergeRow(ws, wb, sr, 0, 3,
                                "Tổng số dịch vụ: " + services.size(),
                                boldFont(wb, C_GREEN, 11), BG_HEADER, HorizontalAlignment.LEFT);

                int[] widths = { 5, 28, 18, 20 };
                for (int i = 0; i < widths.length; i++)
                        ws.setColumnWidth(i, widths[i] * 256);
        }

        // ════════════════════════════════════════════════════════════════════
        // SHEET 5 – NHÂN VIÊN
        // ════════════════════════════════════════════════════════════════════
        private void buildSheetNhanVien(XSSFWorkbook wb, List<User> users) {
                XSSFSheet ws = wb.createSheet("Nhân Viên");
                ws.setDisplayGridlines(false);

                setRowHeight(ws, 0, 36);
                mergeRow(ws, wb, 0, 0, 4,
                                "👥  DANH SÁCH NHÂN VIÊN",
                                boldFont(wb, C_BLUE, 14), BG_HEADER, HorizontalAlignment.LEFT);

                String[] headers = { "#", "Họ và tên", "Username", "Vai trò", "SĐT" };
                XSSFRow hRow = ws.createRow(1);
                setRowHeight(ws, 1, 22);
                for (int i = 0; i < headers.length; i++) {
                        XSSFCell c = hRow.createCell(i);
                        c.setCellValue(headers[i]);
                        c.setCellStyle(headerCellStyle(wb));
                }

                Map<String, String> roleColor = Map.of(
                                "ADMIN", C_RED,
                                "RECEPTIONIST", C_BLUE,
                                "STAFF", C_GREEN);
                Map<String, String> roleBg = Map.of(
                                "ADMIN", "3D1212",
                                "RECEPTIONIST", "0F2744",
                                "STAFF", "0F2E1A");

                // Đếm theo role để làm summary
                long adminCount = users.stream()
                                .filter(u -> u.getRole() != null && "ADMIN".equals(u.getRole().name()))
                                .count();
                long receptionistCount = users.stream()
                                .filter(u -> u.getRole() != null && "RECEPTIONIST".equals(u.getRole().name()))
                                .count();
                long staffCount = users.stream()
                                .filter(u -> u.getRole() != null && "STAFF".equals(u.getRole().name()))
                                .count();

                for (int i = 0; i < users.size(); i++) {
                        User u = users.get(i);
                        XSSFRow row = ws.createRow(2 + i);
                        setRowHeight(ws, 2 + i, 20);
                        String rowBg = i % 2 == 0 ? BG_DARK : BG_MID;
                        String roleName = u.getRole() != null ? u.getRole().name() : "";
                        String rColor = roleColor.getOrDefault(roleName, C_WHITE);
                        String rBg = roleBg.getOrDefault(roleName, rowBg);

                        setCellStr(row, 0, String.valueOf(i + 1),
                                        normalFont(wb, C_MUTED, 10), rowBg);
                        setCellStr(row, 1, nvl(u.getFullName()),
                                        boldFont(wb, C_WHITE, 10), rowBg);
                        setCellStr(row, 2, nvl(u.getUsername()),
                                        normalFont(wb, C_WHITE, 10), rowBg);
                        setCellStr(row, 3, roleName,
                                        boldFont(wb, rColor, 10), rBg);
                        setCellStr(row, 4, nvl(u.getPhoneNumber()),
                                        normalFont(wb, C_MUTED, 10), rowBg);
                }

                // Summary theo role
                int sr = users.size() + 3;
                setRowHeight(ws, sr, 20);
                mergeRow(ws, wb, sr, 0, 4,
                                "📊  THỐNG KÊ NHÂN VIÊN",
                                boldFont(wb, C_BLUE, 11), BG_HEADER, HorizontalAlignment.LEFT);

                Object[][] summary = {
                                { "Tổng nhân viên", (long) users.size(), C_WHITE },
                                { "Admin", adminCount, C_RED },
                                { "Receptionist", receptionistCount, C_BLUE },
                                { "Staff", staffCount, C_GREEN },
                };
                for (int i = 0; i < summary.length; i++) {
                        int r = sr + 1 + i;
                        setRowHeight(ws, r, 18);
                        XSSFRow row = ws.createRow(r);
                        setCellStr(row, 0, (String) summary[i][0],
                                        normalFont(wb, C_MUTED, 10), BG_MID);
                        XSSFCell vc = row.createCell(1);
                        vc.setCellValue(((Number) summary[i][1]).longValue());
                        vc.setCellStyle(numStyle(wb, (String) summary[i][2], BG_MID));
                        for (int c = 2; c <= 4; c++)
                                setCellStr(row, c, "", normalFont(wb, C_WHITE, 10), BG_MID);
                }

                int[] widths = { 5, 26, 18, 16, 16 };
                for (int i = 0; i < widths.length; i++)
                        ws.setColumnWidth(i, widths[i] * 256);
        }

        // ════════════════════════════════════════════════════════════════════
        // HELPER METHODS
        // ════════════════════════════════════════════════════════════════════
        private void buildStatCard(XSSFSheet ws, XSSFWorkbook wb,
                        int startRow, int startCol,
                        String title, String value, String sub, String valColor) {
                int endCol = startCol + 2;

                // Fill background cho toàn bộ block trước
                for (int r = startRow; r <= startRow + 4; r++) {
                        Row row = ws.getRow(r);
                        if (row == null)
                                row = ws.createRow(r);
                        for (int c = startCol; c <= endCol; c++) {
                                Cell cell = row.createCell(c);
                                cell.setCellStyle(plainStyle(wb, BG_MID));
                        }
                }

                // Row 0: title
                mergeRow(ws, wb, startRow, startCol, endCol,
                                "  " + title,
                                normalFont(wb, C_MUTED, 9), BG_MID, HorizontalAlignment.LEFT);

                // Row 1-3: value (merge 3 dòng) — KHÔNG gọi mergeRow, tự xử lý để tránh overlap
                ws.addMergedRegion(new CellRangeAddress(
                                startRow + 1, startRow + 3, startCol, endCol));
                Row valRow = ws.getRow(startRow + 1);
                if (valRow == null)
                        valRow = ws.createRow(startRow + 1);
                Cell valCell = valRow.createCell(startCol);
                valCell.setCellValue(value);
                XSSFCellStyle valStyle = wb.createCellStyle();
                valStyle.setFont(boldFont(wb, valColor, 20));
                valStyle.setFillForegroundColor(hexToXSSF(wb, BG_MID));
                valStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                valStyle.setAlignment(HorizontalAlignment.LEFT);
                valStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                valCell.setCellStyle(valStyle);

                // Row 4: sub text
                mergeRow(ws, wb, startRow + 4, startCol, endCol,
                                "  " + sub,
                                italicFont(wb, C_MUTED, 9), BG_MID, HorizontalAlignment.LEFT);
        }

        private void mergeRow(XSSFSheet ws, XSSFWorkbook wb,
                        int row, int c1, int c2, String value,
                        XSSFFont font, String bg, HorizontalAlignment align) {

                if (c1 < c2)
                        ws.addMergedRegion(new CellRangeAddress(row, row, c1, c2));

                Row r = ws.getRow(row);
                if (r == null)
                        r = ws.createRow(row);
                Cell cell = r.createCell(c1);
                cell.setCellValue(value);

                XSSFCellStyle style = wb.createCellStyle();
                style.setFont(font);
                style.setFillForegroundColor(hexToXSSF(wb, bg));
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setAlignment(align);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                cell.setCellStyle(style);
        }

        private void setCellStr(XSSFRow row, int col, String value,
                        XSSFFont font, String bg) {
                XSSFCell c = row.createCell(col);
                c.setCellValue(value);
                XSSFWorkbook wb = row.getSheet().getWorkbook();
                XSSFCellStyle style = wb.createCellStyle();
                style.setFont(font);
                style.setFillForegroundColor(hexToXSSF(wb, bg));
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                c.setCellStyle(style);
        }

        private XSSFCellStyle headerCellStyle(XSSFWorkbook wb) {
                XSSFCellStyle s = wb.createCellStyle();
                s.setFont(boldFont(wb, C_WHITE, 10));
                s.setFillForegroundColor(hexToXSSF(wb, BG_TBL));
                s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                s.setAlignment(HorizontalAlignment.CENTER);
                s.setVerticalAlignment(VerticalAlignment.CENTER);
                return s;
        }

        private XSSFCellStyle currencyStyle(XSSFWorkbook wb, String bg) {
                return currencyStyle(wb, bg, C_GOLD);
        }

        private XSSFCellStyle currencyStyle(XSSFWorkbook wb, String bg, String color) {
                XSSFCellStyle s = wb.createCellStyle();
                s.setFont(boldFont(wb, color, 10));
                s.setFillForegroundColor(hexToXSSF(wb, bg));
                s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                s.setAlignment(HorizontalAlignment.CENTER);
                s.setVerticalAlignment(VerticalAlignment.CENTER);
                DataFormat fmt = wb.createDataFormat();
                s.setDataFormat(fmt.getFormat("#,##0"));
                return s;
        }

        private XSSFCellStyle numStyle(XSSFWorkbook wb, String color, String bg) {
                XSSFCellStyle s = wb.createCellStyle();
                s.setFont(boldFont(wb, color, 12));
                s.setFillForegroundColor(hexToXSSF(wb, bg));
                s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                s.setAlignment(HorizontalAlignment.CENTER);
                s.setVerticalAlignment(VerticalAlignment.CENTER);
                return s;
        }

        private XSSFCellStyle plainStyle(XSSFWorkbook wb, String bg) {
                XSSFCellStyle s = wb.createCellStyle();
                s.setFillForegroundColor(hexToXSSF(wb, bg));
                s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                return s;
        }

        private XSSFFont boldFont(XSSFWorkbook wb, String hex, int size) {
                XSSFFont f = wb.createFont();
                f.setFontName("Arial");
                f.setBold(true);
                f.setColor(hexToXSSF(wb, hex));
                f.setFontHeightInPoints((short) size);
                return f;
        }

        private XSSFFont normalFont(XSSFWorkbook wb, String hex, int size) {
                XSSFFont f = wb.createFont();
                f.setFontName("Arial");
                f.setColor(hexToXSSF(wb, hex));
                f.setFontHeightInPoints((short) size);
                return f;
        }

        private XSSFFont italicFont(XSSFWorkbook wb, String hex, int size) {
                XSSFFont f = wb.createFont();
                f.setFontName("Arial");
                f.setItalic(true);
                f.setColor(hexToXSSF(wb, hex));
                f.setFontHeightInPoints((short) size);
                return f;
        }

        private XSSFColor hexToXSSF(XSSFWorkbook wb, String hex) {
                byte[] rgb = new byte[] {
                                (byte) Integer.parseInt(hex.substring(0, 2), 16),
                                (byte) Integer.parseInt(hex.substring(2, 4), 16),
                                (byte) Integer.parseInt(hex.substring(4, 6), 16)
                };
                return new XSSFColor(rgb, wb.getStylesSource().getIndexedColors());
        }

        private void setRowHeight(XSSFSheet ws, int rowIdx, int height) {
                Row r = ws.getRow(rowIdx);
                if (r == null)
                        r = ws.createRow(rowIdx);
                r.setHeightInPoints(height);
        }

        private String nvl(String s) {
                return s != null ? s : "";
        }

        private String formatVnd(BigDecimal val) {
                if (val == null)
                        return "0 ₫";
                return String.format("%,.0f ₫", val);
        }

        private BigDecimal getPrice(BookingDetailDashboardDTO bd) {
                return bd.getRoomTypePrice(); // ← trả về giá thực thay vì null
        }
}