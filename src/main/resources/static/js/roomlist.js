

function renderBookingList(data) {
    const tbody = document.querySelector('#roomTable tbody');
    const template = document.getElementById('booking-template');

    tbody.innerHTML = ''; // Xóa trắng bảng trước khi render

    data.forEach(booking => {
        const clone = template.content.cloneNode(true);
        const tr = clone.querySelector('tr');

        // Đổ dữ liệu vào hàng chính
        tr.querySelector('.bid').textContent = `#${booking.bookingId}`;
        tr.querySelector('.name').textContent = booking.customerName;
        tr.querySelector('.id-card').textContent = `ID: ${booking.customerIDCard}`;
        tr.querySelector('.phone').textContent = booking.customerPhone;
        tr.querySelector('.email').textContent = booking.customerEmail;
        
        const statusBadge = tr.querySelector('.status-badge');
        statusBadge.textContent = "Paid"; // Hoặc logic status của bạn

        // Gán sự kiện click để hiện detail
        tr.addEventListener('click', function() {
            toggleDetailRow(this, booking);
        });

        tbody.appendChild(tr);
    });
}

function toggleDetailRow(clickedRow, booking) {
    const nextRow = clickedRow.nextElementSibling;

    // 1. Nếu hàng tiếp theo chính là hàng detail của nó -> Đóng lại
    if (nextRow && nextRow.classList.contains('detail-row')) {
        nextRow.remove();
        clickedRow.classList.remove('active-row');
        return;
    }

    // 2. Đóng TẤT CẢ các hàng detail khác đang mở và xóa class active
    document.querySelectorAll('.detail-row').forEach(row => row.remove());
    document.querySelectorAll('.active-row').forEach(row => row.classList.remove('active-row'));

    // 3. Tạo hàng detail mới
    const template = document.getElementById('detail-template');
    const clone = template.content.cloneNode(true);
    
    // Tạo một hàng tr mới để chứa nội dung từ template
    const detailRow = document.createElement('tr');
    detailRow.classList.add('detail-row');
    
    // Lấy nội dung bên trong template (là các thẻ td) bỏ vào tr mới
    detailRow.innerHTML = clone.querySelector('td').outerHTML;

    // 4. Đổ dữ liệu vào bảng con
    const detailBody = detailRow.querySelector('.detail-body');
    if (booking.details && booking.details.length > 0) {
        booking.details.forEach(d => {
            const rowHtml = `
                <tr>
                    <td>${d.bookingDetailId}</td>
                    <td><span class="status-badge">${d.status}</span></td>
                    <td><strong>${d.roomNumber || 'N/A'}</strong></td>
                    <td>${formatDateTime(d.checkIn)}</td>
                    <td>${formatDateTime(d.checkOut)}</td>
                    <td><button class="action">Edit</button></td>
                </tr>
            `;
            detailBody.insertAdjacentHTML('beforeend', rowHtml);
        });
    } else {
        detailBody.innerHTML = '<tr><td colspan="5" style="text-align:center;">Không có chi tiết phòng</td></tr>';
    }

    // 5. Hiển thị
    clickedRow.classList.add('active-row');
    clickedRow.after(detailRow);
}

function formatDateTime(isoString) {
    if (!isoString || isoString === "-") return "-";

    const date = new Date(isoString);
    
    // Lấy tên Thứ bằng tiếng Việt
    const days = ["CN", "T2", "T3", "T4", "T5", "T6", "T7"];
    const dayName = days[date.getDay()];

    // Format ngày/tháng/năm
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();

    return `${dayName}, ${day}/${month}/${year}`;
}
