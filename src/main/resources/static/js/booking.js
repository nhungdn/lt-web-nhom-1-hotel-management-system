async function renderEditForm(btn) {
    const popup = document.querySelector(".popup");
    const form = document.querySelector("#edit-form");

    const overlay = document.getElementById("popupOverlay");
    overlay.classList.add("active");

    const bookingId = Number(btn.dataset.bookingId);
    const detailId = Number(btn.dataset.detailId);
    form.dataset.detailId = detailId;

    const booking = bookingData.find(b => b.bookingId === bookingId);
    const detail = booking.details.find(bd => bd.bookingDetailId === detailId);

    form.dataset.detailId = detailId;
    form.dataset.roomId = detail.roomId;
    form.dataset.rawCheckIn = detail.checkIn;
    form.dataset.rawCheckOut = detail.checkOut;
    // Đổ dữ liệu text vào đúng các div/span
    form.querySelector('.roomNumber').textContent = `Phòng: ${detail.roomNumber}`;
    form.querySelector('.checkIn').textContent = formatDateTime(detail.checkIn);
    form.querySelector('.checkOut').textContent = formatDateTime(detail.checkOut);
    form.querySelector('.customer .name').textContent = booking.customerName;
    form.querySelector('.customer .id-card').textContent = "ID Card: " + booking.customerIDCard;
    form.querySelector('.customer .phone').textContent = "SĐT: " + booking.customerPhone;
    form.querySelector('.customer .email').textContent = "Email: " + booking.customerEmail;

    const container = form.querySelector(".service-container");
    const template = container.querySelector("template");

    // Xóa các hàng dịch vụ cũ nhưng giữ lại nút "Edit Service", "Add" và "Template"
    // Ta xóa tất cả div.service mà KHÔNG chứa chữ "Add"
    const oldItems = container.querySelectorAll(".service");
    oldItems.forEach(item => {
        if (item.textContent.trim() !== "Add" && !item.classList.contains('new-service-item')) {
            item.remove();
        }
    });

    // Render dịch vụ hiện có
    if (detail.services) {
        detail.services.forEach(s => {
            const clone = template.content.cloneNode(true);
            const div = clone.querySelector(".service");

            div.dataset.serviceId = s.hotelServiceId;
            div.querySelector(".name").textContent = s.serviceName;
            div.querySelector(".quantity").value = s.quantity;
            div.querySelector(".price").textContent = new Intl.NumberFormat('vi-VN').format(s.price) + "đ";

            // Chèn vào TRƯỚC nút Add
            const addBtn = Array.from(container.querySelectorAll(".service")).find(el => el.textContent === "Add");
            container.insertBefore(clone, addBtn);
        });
    }

    // Xử lý nút Add
    const addBtn = Array.from(container.querySelectorAll(".service")).find(el => el.textContent === "Add");
    addBtn.onclick = () => {
        renderNewServiceRow(container, template); // Truyền container vào đây
    };

    // Nút reset
    form.querySelector('button[type="reset"]').onclick = (e) => {
        e.preventDefault();
        overlay.classList.remove("active");
    };

    overlay.onclick = (e) => {
        if (e.target === overlay) {
            overlay.classList.remove("active");
        }
    };

    form.querySelector('.cancel').onclick = (e) => {
        e.preventDefault(); // Chống reload trang
        if (confirm("Xác nhận hủy phòng này?")) {
            sendCancelForm(detailId);
        }
    };
}

function renderNewServiceRow(container, template) {
    const row = document.createElement('div');
    row.className = 'service new-service-item';

    let options = allServices.map(s =>
        `<option value="${s.serviceId}">${s.name} (${new Intl.NumberFormat('vi-VN').format(s.price)}đ)</option>`
    ).join('');

    row.innerHTML = `
        <label>
            <select class="new-service-id" style="margin-right: 5px;">
                <option value="">-- Chọn dịch vụ --</option>
                ${options}
            </select>
            <input type="number" class="quantity" value="1" min="1" style="width: 50px;"></input>
        </label>
        <button type="button" class="remove-service" style="color:red; background:none; border:none; cursor:pointer;">&times;</button>
    `;

    row.querySelector('.remove-service').onclick = () => row.remove();

    // Chèn vào TRƯỚC nút Add
    const addBtn = Array.from(container.querySelectorAll(".service")).find(el => el.textContent === "Add");
    container.insertBefore(row, addBtn);
}

async function sendEditForm(e) {
    e.preventDefault();
    const form = e.target;
    const container = form.querySelector(".service-container");

    // 1. Thu thập dịch vụ cũ (những div.service có sẵn ID)
    const existingServices = Array.from(container.querySelectorAll(".service"))
        .filter(el => el.dataset.serviceId) // Chỉ lấy hàng có ID dịch vụ
        .map(el => ({
            hotelServiceId: Number(el.dataset.serviceId),
            quantity: Number(el.querySelector(".quantity").value)
        }));

    // 2. Thu thập dịch vụ mới (những div được add thêm có select)
    const newServices = Array.from(container.querySelectorAll(".new-service-item")).map(el => ({
        hotelServiceId: Number(el.querySelector(".new-service-id").value),
        quantity: Number(el.querySelector(".quantity").value)
    })).filter(s => s.hotelServiceId > 0);

    // 3. Chuẩn bị payload khớp với BookingDetailDTO.java
    const payload = {
        // Mặc dù popup sửa 1 phòng, nhưng Service đang lặp qua List<DetailDTO>
        details: [{
            bookingDetailId: Number(form.dataset.detailId),
            roomId: Number(form.dataset.roomId), // Bạn nhớ gán roomId vào dataset ở hàm render
            checkIn: form.dataset.rawCheckIn,    // Dùng định dạng ISO gốc từ DB (LocalDateTime.parse cần cái này)
            checkOut: form.dataset.rawCheckOut,  // Không dùng chuỗi đã format tiếng Việt "T3, 21/04..."
            status: "PENDING", // Hoặc lấy từ 1 select status nếu có
            services: [...existingServices, ...newServices]
        }]
    };

    const token = document.querySelector('meta[name="_csrf"]')?.content;
    const header = document.querySelector('meta[name="_csrf_header"]')?.content;
    try {
        const response = await fetch('/booking/edit', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', [header]: token },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            alert("Cập nhật thành công!");
            location.reload();
        } else {
            const errors = await response.json();
            alert("Lỗi: " + errors.join("\n"));
        }
    } catch (err) {
        console.error("Gửi form thất bại:", err);
    }
}

document.querySelector("#edit-form").onsubmit = sendEditForm;

async function sendCancelForm(id) {
    const token = document.querySelector('meta[name="_csrf"]')?.content;
    const header = document.querySelector('meta[name="_csrf_header"]')?.content;
    try {
        const response = await fetch('/booking/cancel', {
            method: 'POST',
            headers: {'Content-Type': 'application/json', [header]: token},
            body: JSON.stringify({id: id, isDetail: true})
        });

        if (response.ok) {
            alert("Cập nhật thành công!");
            location.reload();
        } else {
            const errors = await response.json();
            alert("Lỗi: " + errors.join("\n"));
        }
    } catch (err) {
        console.error("Gửi form thất bại:", err);
    }
}