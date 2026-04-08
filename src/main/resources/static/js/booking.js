document.addEventListener("DOMContentLoaded", () => {
    const enterBookingBtn = document.querySelector(".booking-btn");
    const booktab = document.querySelectorAll(".booktab");
    const roomtab = document.querySelectorAll(".roomtab");
    
    console.log("Booking script has run.");
    
    enterBookingBtn.addEventListener("click", () => {
        console.log("Toggle!"); 
        booktab.forEach((e) => e.classList.toggle("hidden"));
        roomtab.forEach((e) => e.classList.toggle("hidden"));
    });
    
    const bookForm = document.querySelector(".form-overlay");
    document.querySelector("#custInfo").addEventListener("click", () =>{
        bookForm.classList.toggle("active");
    });
    
    const roomRow = document.querySelectorAll("tbody tr");
    const detailPopup = document.querySelector(".detail-overlay");
    const closeBtn = document.querySelectorAll(".close-btn");
    roomRow.forEach((row)=>{
        row.addEventListener('click', (e) => {
            if (e.target.type === 'checkbox' || e.target.tagName === 'BUTTON') {
                return;
            }
            console.log('Row clicked! Show popup');
            detailPopup.style.display = "block";
        });
    });
    
    closeBtn.forEach(btn => {
        btn.addEventListener("click", () => {
            detailPopup.style.display = "none"; // Hoặc .classList.add("hidden")
        });
    });
    
    function doBook(id, checkin, checkout, price) {
        const mainForm = document.querySelector('.booking-form form'); // Form có sẵn trong fragment

        // Tạo một div chứa thông tin phòng này để dễ quản lý
        const itemContainer = document.createElement('div');
        itemContainer.classList.add('booking-item-data');

        itemContainer.innerHTML = `
        <input type='hidden' name='roomNumbers' value='${id}'>
        <input type='hidden' name='checkIns' value='${checkin}'> 
        <input type='hidden' name='checkOuts' value='${checkout}'> 
        <input type='hidden' name='price' value='${price}'>
        `;
        mainForm.appendChild(itemContainer);
    }
    
});

document.querySelector('.booking-form form').addEventListener('submit', function(e) {
    e.preventDefault();

    const bookingData = {
        customerName: document.querySelector("#custName").value, // Lấy từ form fragment
        customerPhone: document.querySelector("#custPhone").value,
        bookingItems: []
    };

    // Quét các phòng đã chọn
    document.querySelectorAll("#roomTable tbody tr").forEach(row => {
        const checkbox = row.querySelector("input[type='checkbox']");
        if (checkbox && checkbox.checked) {
            bookingData.bookingItems.push({
                roomNum: row.querySelector(".room-id").textContent,
                checkIn: document.querySelector("#start").value + "T00:00:00", // Format LocalDateTime
                checkOut: document.querySelector("#end").value + "T00:00:00"
            });
        }
    });

    // Gửi bằng Fetch API lên Controller
    fetch('/api/bookings/create', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(bookingData)
    }).then(res => {
        if (res.ok) {
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
        }
    });
    
    if(document.querySelector('.bookstat') === 'success'){
        // Create success message
        

        // Refresh trang
        this.reset();
        window.location.reload();
    }
});

// Add fade in animation
const fadeStyle = document.createElement('style');
fadeStyle.textContent = `
    @keyframes fadeIn {
        from { opacity: 0; transform: translate(-50%, -50%) scale(0.8); }
        to { opacity: 1; transform: translate(-50%, -50%) scale(1); }
    }
`;
document.head.appendChild(fadeStyle);