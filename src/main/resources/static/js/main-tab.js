let currentFocusTempId = null;
let currentDaygroup = null;
let currentTabIndex = 0;

// Hàm vẽ lại danh sách dịch vụ khi có dữ liệu mới
async function renderServiceGrid() {
    const response = await fetch('/services/api/all', {
        method: 'GET',
        headers: { 'Content-Type': 'application/json', 'X-XSRF-TOKEN': csrfToken }
    });
    const data = await response.json();
    console.log(data);
    const grid = document.querySelector('.service-grid');
    grid.innerHTML = '';
    data.forEach(s => {
        const html = `
            <div class="hs-card">
                <div data-name="${s.name}" class="hs-name">${s.name}</div>
                <div data-price="${s.price}" class="hs-price">${s.price.toLocaleString()} VNĐ</div>
                <div class="quantity-control">
                    <button class="decrease" data-service-id="${s.serviceId}" 
                    data-name="${s.name}" onclick="changeQuantityService(this)"> - </button>
                    <span class="quantity"> 0 </span>
                    <button class="increase" data-service-id="${s.serviceId}"
                     data-name="${s.name}" onclick="changeQuantityService(this)"> + </button>
                </div>
            </div>`;
        grid.insertAdjacentHTML('beforeend', html);
    });

}

function changeQuantityService(btn) {
    const sid = Number(btn.getAttribute('data-service-id'));
    const sname = btn.getAttribute('data-name');
    const sprice = parseInt(btn.closest('.hs-card').querySelector('.hs-price').textContent.replace(/[^0-9]/g, ''));

    const selectedRoomEl = document.querySelector('.select-sidebar .active'); // Tìm dòng phòng đang được chọn

    if (!selectedRoomEl) {
        alert("Vui lòng click chọn một phòng ở danh sách bên phải trước khi thêm dịch vụ!");
        return;
    }

    const tempId = selectedRoomEl.dataset.tempId;

    // Tìm phòng trong mảng dữ liệu
    let theRoom = null;
    selectedRooms.forEach(group => {
        const found = group.rooms.find(r => r.tempId === tempId);
        if (found) theRoom = found;
    });

    let count = 0;
    if (theRoom) {
        let sIndex = theRoom.services.findIndex(s => s.serviceId === sid);

        if (btn.classList.contains('increase')) {
            if (sIndex === -1) {
                theRoom.services.push({
                    serviceId: parseInt(sid),
                    serviceName: sname,
                    quantity: 1,
                    price: sprice
                });
            } else theRoom.services[sIndex].quantity++;
        } else {
            if (sIndex !== -1) {
                theRoom.services[sIndex].quantity = Math.max(theRoom.services[sIndex].quantity - 1, 0);
                if (theRoom.services[sIndex].quantity === 0) {
                    theRoom.services.splice(sIndex, 1); // Xóa dịch vụ nếu về 0
                }
            }
        }

        const card = btn.closest('.hs-card');
        const count = theRoom.services.find(s => s.serviceId === Number(sid))?.quantity ?? 0;
        updateCardUI(card, count);
        renderSelectedSidebar();
    }
}

function renderSelectedSidebar() {
    const parentContainer = document.querySelector('.selected-room');
    const container = document.querySelector('.select-sidebar');
    const oldRows = container.querySelectorAll('.booking-item-row');
    oldRows.forEach(row => row.remove());

    if(selectedRooms.length > 0){
        parentContainer.classList.add('active');
    } else parentContainer.classList.remove('active');

    const itemTemplate = document.querySelector('#item');
    const riTemplate = document.querySelector("#room-item");
    const siTemplate = document.querySelector("#service-item");

    let totalPrice = 0;

    selectedRooms.forEach(item => {
        // Clone cái vỏ bọc ngày (Day Group)
        let itemNode = itemTemplate.content.cloneNode(true);
        let rowDiv = itemNode.querySelector('.booking-item-row');

        rowDiv.querySelector('.day').textContent = `${item.start} | ${item.end}`;
        const roomListUl = rowDiv.querySelector('ul');

        if(currentDaygroup && item.start === currentDaygroup[0] && item.end === currentDaygroup[1])
            rowDiv.classList.add('active');
        rowDiv.querySelector('.day').onclick = () => {
            if (currentTabIndex === 0) {
                currentFocusTempId = null;
                currentDaygroup = [item.start, item.end];
                setDate(item.start, item.end);
                renderRoomQuantity();
            }
            renderSelectedSidebar();
        };

        // Lặp qua từng phòng
        item.rooms.forEach(r => {
            let rnode = riTemplate.content.cloneNode(true);
            let rLi = rnode.querySelector('li');

            rLi.querySelector('.roomname').textContent = r.roomTypeName;
            rLi.querySelector('.price').textContent = Number(r.price).toLocaleString() + " VNĐ";
            totalPrice += Number(r.price);
            rLi.dataset.tempId = r.tempId;

            // Highlight nếu đang được chọn để thêm dịch vụ
            if (r.tempId === currentFocusTempId) rLi.classList.add('active');

            // Sự kiện click để chọn phòng này làm "Focus" để thêm dịch vụ
            rLi.onclick = () => {
                if (currentTabIndex === 1) {
                    currentDaygroup = null;
                    currentFocusTempId = r.tempId;
                    setDate(item.start, item.end);
                    renderServiceQuantity(rLi);
                }
                renderSelectedSidebar();
            };

            const serviceListUl = rLi.querySelector('ul');
            // Dịch vụ của từng phòng
            (r.services || []).forEach(s => {
                let snode = siTemplate.content.cloneNode(true);
                let sLi = snode.querySelector('li');
                sLi.querySelector('.name').textContent = s.serviceName;
                sLi.querySelector('.amount').textContent = 'x' + s.quantity;
                sLi.querySelector('.price').textContent = Number(s.price);
                totalPrice += s.quantity * s.price;
                sLi.dataset.serviceId = s.serviceId;
                serviceListUl.appendChild(sLi);
            });
            roomListUl.appendChild(rLi);
        });
        container.insertBefore(rowDiv, container.querySelector('.total-and-toggle'));
    });

    //Tính tổng giá
    container.querySelector('.total-and-toggle h6').textContent = totalPrice.toLocaleString() + ' VNĐ';

}

function validateInput1() {
    const startInput = document.querySelector('#start');
    const endInput = document.querySelector('#end');
    const customerForm = document.querySelector('#customerForm');

    const hiddenStart = startInput.parentElement.querySelector('.filter-error');
    const hiddenEnd = endInput.parentElement.querySelector('.filter-error');
    const hiddenForm = document.querySelector('.filter-error-form'); // Đảm bảo class này đúng

    function showErrorMessage(element, msgBox) {
        if(msgBox) msgBox.classList.remove('hidden');
        element.classList.add('input-error');
    }

    // Gán sự kiện ẩn lỗi ngay khi khởi tạo
    [startInput, endInput, customerForm].forEach(input => {
        if (!input.dataset.hasListener) { // Tránh gán trùng lặp sự kiện
            input.addEventListener('input', () => {
                const errorBox = input.parentElement.querySelector('.filter-error') || hiddenForm;
                if(errorBox) errorBox.classList.add('hidden');
                input.classList.remove('input-error');
            });
            input.dataset.hasListener = "true";
        }
    });

    let isAllValid = true;

    if (!startInput.checkValidity()) {
        showErrorMessage(startInput, hiddenStart);
        isAllValid = false;
    }
    if (!endInput.checkValidity()) {
        showErrorMessage(endInput, hiddenEnd);
        isAllValid = false;
    }
    if (customerForm && !customerForm.checkValidity()) {
        showErrorMessage(customerForm, hiddenForm);
        isAllValid = false;
    }

    // Ngày kết thúc phải sau ngày bắt đầu
    if (startInput.value && endInput.value) {
        if (new Date(endInput.value) <= new Date(startInput.value)) {
            showErrorMessage(endInput, hiddenEnd);
            // Bạn có thể đổi text thông báo lỗi ở đây nếu muốn cụ thể hơn
            isAllValid = false;
        }
    }

    return isAllValid; // Trả về kết quả cuối cùng
}

document.addEventListener('DOMContentLoaded', () => {
    switchTab(0);
    const tabControl = document.querySelector(".select-sidebar .total-and-toggle");
    const forwardBtn = tabControl.querySelector(".forward");
    const backBtn = tabControl.querySelector(".back");

    filterBtn.addEventListener('click', filter);

    forwardBtn.addEventListener('click', () => {
        if (validateInput1()) {
            if (selectedRooms.length === 0) {
                alert("Vui lòng chọn ít nhất một phòng trước khi tiếp tục!");
                return;
            }
            switchTab(1);
        } else console.log("Khách quên điền hết form.");
    });
    
    backBtn.addEventListener('click', () => {
        switchTab(0);
    });

});

function switchTab(tabIndex) {
    const grids = [document.querySelector('.roomtypes-grid'), document.querySelector('.service-grid')];
    const startInput = document.querySelector('#start');
    const endInput = document.querySelector('#end');
    const customerFormBtn = document.querySelector('#custInfo');

    // Nút điều hướng
    const forwardBtn = document.querySelector(".forward");
    const backBtn = document.querySelector(".back");
    const finishBtn = document.querySelector(".finish");

    grids.forEach((g, i) => {
        if (g) g.classList.toggle('hidden', i !== tabIndex);
    });

    if (tabIndex === 0) {
        if(backBtn) backBtn.disabled = true;
        if(forwardBtn) forwardBtn.classList.remove('hidden');
        if(finishBtn) finishBtn.classList.add('hidden');
        startInput.readOnly = false;
        endInput.readOnly = false;
    } else {
        if(backBtn) backBtn.disabled = false;
        if(forwardBtn) forwardBtn.classList.add('hidden');
        if(finishBtn) finishBtn.classList.remove('hidden');
        startInput.readOnly = true;
        endInput.readOnly = true;
        renderServiceGrid();
    }
    currentTabIndex = tabIndex;
}

/*========================================

 Script re-filter lại phòng, dịch vụ onclick sidebar

=========================================*/
// <div selected-sidebar>
//      #trên đây là 1 hàng booking, h6 có startdate enddate
//     <div .booking-item-row>
//         <h6></h6>
//         <ul> #ul này là list phòng đặt trg khoảng h6, lưu tempId
//             <li data-temp-id room>
//                 <div .roomname .price></div>
//                 <ul> #ul có service đã chọn
//                      <li data-service-id></li>
//                 </ul>
//             </li>
//         </ul>
//     </div>
// </div>

