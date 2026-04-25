document.addEventListener('DOMContentLoaded', () => {
    const finishBtn = document.querySelector(".finish");
    if (finishBtn) {
        finishBtn.addEventListener('click', async (e) => {
            e.preventDefault();
            console.log("selectedRooms hiện tại:", selectedRooms);
            await handleFinish();
        });
    }
});

async function handleFinish() {
    // 1. Thu thập thông tin khách hàng từ Form
    const customerName = document.querySelector('input[name="name"]')?.value;
    const customerPhone = document.querySelector('input[name="phone"]')?.value;
    const customerEmail = document.querySelector('input[name="email"]')?.value;
    const customerIdCard = document.querySelector('input[name="id_card"]')?.value;

    // 2. Chuyển đổi cấu trúc selectedRooms sang DTO mà Java mong đợi
    // Cấu trúc: List<BookingItem> { roomTypeId, quantity: 1, checkIn, checkOut, serviceItems }
    const bookingItems = [];
    selectedRooms.forEach(group => {
        group.rooms.forEach(room => {
            bookingItems.push({
                roomTypeId: room.roomTypeId, // Khớp Long
                quantity: 1,                 // Khớp int
                checkIn: group.start,        // Khớp String (ISO date: yyyy-MM-dd)
                checkOut: group.end,         // Khớp String
                serviceItems: room.services.map(s => ({
                    serviceId: s.serviceId,  // Khớp Long
                    quantity: s.quantity     // Khớp int
                }))
            });
        });
    });

    const requestBody = {
        customerName: customerName,
        customerPhone: customerPhone,
        customerEmail: customerEmail,
        customerIdCard: customerIdCard,
        bookingItems: bookingItems
    };

    // 3. Gửi API POST
    const token = document.querySelector('meta[name="_csrf"]')?.content;
    const header = document.querySelector('meta[name="_csrf_header"]')?.content;
    console.log("bây h gửi thử form");
    try {
        const response = await fetch('/book', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', [header]: token },
            body: JSON.stringify(requestBody)
        });

        if (response.ok) {
            // Hiện message thành công
            const successMsg = document.createElement('div');
            successMsg.style.cssText = `
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: rgba(46, 204, 113, 0.9);
            color: white;
            padding: 20px 40px;
            border-radius: 10px;
            backdrop-filter: blur(20px);
            z-index: 10000;
            animation: fadeIn 0.3s ease;
            `;
            successMsg.textContent = 'ĐÃ LƯU FORM THÀNH CÔNG!';

            document.body.appendChild(successMsg);

            // Remove message after 3 seconds
            setTimeout(() => {
                successMsg.remove();
            }, 3000);

            window.location.reload();
        } else {
            const errors = await response.json();
            alert("Đặt phòng thất bại: " + (Array.isArray(errors) ? errors.join(", ") : errors));
        }
    } catch (err) {
        console.error("Lỗi kết nối:", err);
        alert("Có lỗi xảy ra khi kết nối tới máy chủ.");
    }
    console.log("đã thử form và nhận phản hồi?");
}

// Add fade in animation
const fadeStyle = document.createElement('style');
fadeStyle.textContent = `
    @keyframes fadeIn {
        from { opacity: 0; transform: translate(-50%, -50%) scale(0.8); }
        to { opacity: 1; transform: translate(-50%, -50%) scale(1); }
    }
`;
document.head.appendChild(fadeStyle);