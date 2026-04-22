/*========================================

 Script lọc phòng trống theo ngày

=========================================*/
let selectedRooms = [];
// Cấu trúc mong muốn:
// selectedRooms = [
// {
//     start: '2026-01-1',
//     end: '2026-01-1',
//     room: [
//         {tempID: datestamp+ '_'+ roomTypeID, roomTypeId: 1,
//         roomTypeName: 'Deluxe', price: 10000,
//             services: [{serviceId: 1, serviceName: 'Breakfast', quantity: 3}]
//         },
//         {tempID: datestamp+ '_'+ roomTypeID, roomTypeId: 1,
//         roomTypeName: 'Deluxe', price: 10000,
//             services: [{serviceId: 2, serviceName: 'Breakfast',quantity: 3}]
//         }
//     ]
// }]

// hàm lấy ngày từ input
function getDate() {
    const startDate = document.querySelector('#start').value;
    const endDate = document.querySelector('#end').value;
    return { startDate, endDate };
}
function setDate(startDate, endDate) {
    document.querySelector('#start').value = startDate;
    document.querySelector('#end').value = endDate;
}

//lấy cookie để ko bị chặn fetch
const getCookie = (name) => {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
    };
const csrfToken = getCookie('XSRF-TOKEN'); // Spring trả về tên này

async function filter() {
    const { startDate, endDate } = getDate();
    if (!startDate || !endDate) {
        alert("Vui lòng chọn đầy đủ ngày!");
        return;
    }
    if(!validateDates()) return;

    try {
        const response = await fetch('/filter', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'X-XSRF-TOKEN': csrfToken },
            body: JSON.stringify({ checkIn: startDate, checkOut: endDate })
        });
        const data = await response.json();

        if (response.ok) {
            updateRoomGrid(data.roomTypes, data.roomTypeImages);
            renderRoomQuantity();
        } else {
            console.error(data);
        }
    } catch (err) {
        console.error("Lỗi kết nối:", err);
    }
}

const filterBtn = document.querySelector("#filterdateBtn");
filterBtn.addEventListener('click',  () => {
    filter();
    //kiểm tra đặt phòng để hiện warning
    const warningDiv = document.querySelector('.filter-date-container .warning');
    if(selectedRooms.length > 0) warningDiv.classList.remove('hidden');
    else warningDiv.classList.add('hidden');
});

// Hàm vẽ lại danh sách phòng khi có dữ liệu mới
function updateRoomGrid(roomTypes, roomTypeImages) {
    const grid = document.querySelector('.roomtypes-grid');
    grid.innerHTML = ''; // Xóa sạch danh sách cũ

    roomTypes.forEach(rt => {
        const images = roomTypeImages[rt.roomTypeId] || [];
        const imgSrc = images.length > 0 ? images[0].imageUrl : 'data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' viewBox=\'0 0 400 300\'%3E%3Crect fill=\'%23374151\' width=\'400\' height=\'300\'/%3E%3Ctext x=\'50%\' y=\'50%\' font-family=\'Arial\' font-size=\'24\' fill=\'%239CA3AF\' text-anchor=\'middle\' dominant-baseline=\'middle\'%3ENo Image Available%3C/text%3E%3C/svg%3E';

        const html = `
            <div class="roomtype-card">
                <img src="${imgSrc}" class="roomtype-image" alt="Room">
                <div class="roomtype-body">
                    <div>
                        <h3 class="roomtype-name">${rt.name}</h3>
                        <p class="roomtype-description">${rt.description}</p>
                    </div>
                    <!-- chỉnh số lượng phòng ở đây -->
                    <div class="quantity-control hidden">
                        <button class="decrease" data-id="${rt.roomTypeId}" 
                            data-name="${rt.name}" data-price="${rt.price}"
                            onclick="changeQuantityRoom(this)"> - </button>
                        <span class="quantity">0</span>
                        <button class="increase" data-id="${rt.roomTypeId}" 
                            data-name="${rt.name}" data-price="${rt.price}"
                            onclick="changeQuantityRoom(this)"> + </button>
                    </div>
                    <div class="roomtype-price">${rt.price.toLocaleString()} VNĐ/night</div>
                    <div class="available-info">Còn trống: <span>${rt.availableRooms}</span>/${rt.totalRooms} phòng</div>
                    <!-- nút book với detail phòng ở đây -->
                    <div class="roomtype-actions">
                        <button class="btn-book increase" data-id="${rt.roomTypeId}"
                            data-name="${rt.name}" data-price="${rt.price}" 
                            ${rt.availableRooms === 0 ? 'disabled' : ''} 
                            onclick="changeQuantityRoom(this)">Book Now</button>
                        <button class="btn-view-more" 
                            data-id="${rt.roomTypeId}"
                            data-name="${rt.name}"
                            data-description="${rt.description}"
                            onclick="viewRoomTypeDetails(this)">
                            View More
                        </button>
                    </div>
                </div>
            </div>`;
        grid.insertAdjacentHTML('beforeend', html);
    });
}

function changeQuantityRoom(btn){
    const id = Number(btn.getAttribute('data-id'));
    const name = btn.getAttribute('data-name');
    const price = btn.getAttribute('data-price');
    const { startDate, endDate } = getDate();
    const isIncrease = btn.classList.contains('increase');

    let dayGroup = selectedRooms.find(r => r.start === startDate && r.end === endDate);
    if (!dayGroup) {
        if (!isIncrease) return;
        dayGroup = { start: startDate, end: endDate, rooms: [] };
        selectedRooms.push(dayGroup);
    }

    if (isIncrease) {
        // Thêm 1 suất phòng mới
        const newRoom = {
            tempId: Date.now() + "_" + id,
            roomTypeId: parseInt(id),
            roomTypeName: name,
            price: price,
            services: []
        };
        dayGroup.rooms.push(newRoom);
    } else {
        // Tìm và xóa phòng cuối cùng cùng loại + cùng ngày
        const index = dayGroup.rooms.findLastIndex(r => r.roomTypeId === id);
        if (index !== -1) {
            dayGroup.rooms.splice(index, 1);
        }
    }

    if (dayGroup.rooms.length === 0) {
        selectedRooms = selectedRooms.filter(g => g !== dayGroup);
    }

    // Tìm các thành phần trong Card
    const card = btn.closest('.roomtype-card');

    const qtyControl = card.querySelector('.quantity-control');
    const qtySpan = card.querySelector('.quantity');
    const bookBtn = card.querySelector('.btn-book');
    let maxAvail = card.querySelector('.available-info span').textContent;
    maxAvail = parseInt(maxAvail) || 0;

    const currentGroup = selectedRooms.find(g => g.start === startDate && g.end === endDate);
    let count = 0;
    if (currentGroup) {
        count = currentGroup.rooms.filter(r => r.roomTypeId === id).length;
    }

    updateCardUI(card, count);
    // Vẽ lại Sidebar
    renderSelectedSidebar();
}

function renderRoomQuantity(){
    const {startDate, endDate} = getDate();
    const dayGroup = selectedRooms.find(gr => gr.start === startDate && gr.end === endDate);
    const cards = document.querySelectorAll('.roomtype-card');

    cards.forEach(card => {
        const id = card.querySelector('.decrease').getAttribute('data-id');
        const qtySpan = card.querySelector('.quantity');
        const qtyControl = card.querySelector('.quantity-control');
        const bookBtn = card.querySelector('.btn-book');
        let maxAvail = card.querySelector('.available-info span').textContent;
        maxAvail = parseInt(maxAvail) || 0;
        let count = 0;
        if (dayGroup) {
            // Đếm xem trong ngày này, loại phòng này đã đặt bao nhiêu suất
            count = dayGroup.rooms.filter(r => r.roomTypeId === Number(id)).length;
        }

        updateCardUI(card, count);
    });
}

function renderServiceQuantity(row){
    const {startDate, endDate} = getDate();
    const dayGroup = selectedRooms.find(gr => gr.start === startDate && gr.end === endDate);

    const tempId = row.getAttribute('data-temp-id');
    const theRoom = dayGroup.rooms.find(r=> r.tempId=== tempId);
    const cards = document.querySelectorAll('.hs-card');
    cards.forEach(card => {
        const serviceId = card.querySelector('.decrease').getAttribute('data-service-id');
        const qtySpan = card.querySelector('.quantity');

        let count;
        if (theRoom) {
            // Đếm xem phòng này, dịch vụ này đã đặt bnhieu
            const foundSvc = theRoom.services.find(s => s.serviceId === Number(serviceId));
            count = foundSvc ? foundSvc.quantity : 0;
        }
        updateCardUI(card, count);
    });
}

function updateCardUI(card, count) {
    const qtyControl = card.querySelector('.quantity-control');
    const qtySpan = card.querySelector('.quantity');
    const bookBtn = card.querySelector('.btn-book');
    const maxAvail = parseInt(card.querySelector('.available-info span')?.textContent) || 1000;

    // 1. Cập nhật con số
    if (qtySpan) qtySpan.textContent = Math.min(count, maxAvail);

    // 2. Ẩn/Hiện điều khiển
    if (count > 0) {
        qtyControl.classList.remove('hidden');
        if (bookBtn) bookBtn.classList.add('hidden');
    } else {
        if (card.classList.contains('.roomtype-card')) qtyControl.classList.add('hidden');
        if (bookBtn) bookBtn.classList.remove('hidden');
    }

    // 3. Khóa nút nếu đạt giới hạn
    const incBtn = qtyControl.querySelector('.increase');
    const decBtn = qtyControl.querySelector('.decrease');
    if (incBtn) incBtn.disabled = (count >= maxAvail);
    if (decBtn) decBtn.disabled = (count === 0);
}