(function ($) {
    "use strict";

    $(document).ready(function($){

        // stikcy js
        $("#sticker").sticky({
            topSpacing: 0
        });
    
    });
}(jQuery));

// Modal functions
function viewRoomTypeDetails(roomTypeId, roomTypeName, description) {
    const modal = document.getElementById('detailsModal');
    const backdrop = document.getElementById('detailsBackdrop'); // Lấy thêm Backdrop
    const title = document.getElementById('detailsTitle');
    const desc = document.getElementById('detailsDescription');
    const imagesGrid = document.getElementById('detailsImagesGrid');
    const loadingMsg = document.getElementById('loadingMessage');

    // Set basic info
    title.textContent = roomTypeName;
    desc.textContent = description || 'No description available';
    imagesGrid.innerHTML = '';
    loadingMsg.style.display = 'block';

    // Fetch images from API
    fetch('/roomtypeimages/api/by-roomtype/' + roomTypeId)
            .then(response => response.json())
            .then(images => {
                loadingMsg.style.display = 'none';

                if (images && images.length > 0) {
                    images.forEach(img => {
                        const imgCard = document.createElement('div');
                        imgCard.className = 'detail-image-card';
                        imgCard.innerHTML = `
                                    <img src="${img.imageUrl}" alt="Room image" class="detail-image"
                                    style="cursor: zoom-in;" 
                                    onclick="openImageLightbox('${img.imageUrl}')"/>
                                    <p class="detail-image-desc">${escapeHtml(img.description || '')}</p>
                                `;
                        imagesGrid.appendChild(imgCard);
                    });
                } else {
                    imagesGrid.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--text-muted);">No images available</p>';
                }
            })
            .catch(error => {
                loadingMsg.style.display = 'none';
                console.error('Error fetching images:', error);
                imagesGrid.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: #ef4444;">Error loading images</p>';
            });

    // Show modal & backdrop (Đã bổ sung active cho backdrop)
    modal.classList.add('active');
    backdrop.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeDetailsModal() {
    const modal = document.getElementById('detailsModal');
    const backdrop = document.getElementById('detailsBackdrop');

    modal.classList.remove('active');
    backdrop.classList.remove('active'); // Đã bổ sung ẩn backdrop
    document.body.style.overflow = '';
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Close modal when clicking backdrop
document.getElementById('detailsBackdrop')?.addEventListener('click', closeDetailsModal);

// Close modal on escape key
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        closeDetailsModal();
    }
});
// --- Các hàm xử lý Lightbox ---
function openImageLightbox(imageSrc) {
    const lightbox = document.getElementById('imageLightbox');
    const lightboxImg = document.getElementById('lightboxImage');

    lightboxImg.src = imageSrc; // Gán link ảnh vào khung to
    lightbox.classList.add('active'); // Hiện khung
}

function closeImageLightbox() {
    const lightbox = document.getElementById('imageLightbox');
    lightbox.classList.remove('active'); // Ẩn khung
}

// Bổ sung: Bấm phím Escape cũng đóng được ảnh to
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        closeImageLightbox();
        // Hàm closeDetailsModal() đã có ở trên sẽ tự chạy để đóng modal ngoài
    }
});

//Phần lọc phòng theo ngày để booking
const filterContainer = document.querySelector(".filter-date-container");
const filterBtn = document.querySelector("#filterdateBtn");
const customerFormBtn = document.querySelector("#custInfo");

// Cập nhật hàm lấy ngày chuẩn xác
function getDate() {
    const startDate = document.querySelector('#start').value;
    const endDate = document.querySelector('#end').value;
    return { startDate, endDate };
}

async function filter() {
    const { startDate, endDate } = getDate();
    if (!startDate || !endDate) {
        alert("Vui lòng chọn đầy đủ ngày!");
        return;
    }

    try {
        const response = await fetch('/filter', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ checkIn: startDate, checkOut: endDate })
        });
        const data = await response.json();
        
        if (response.ok) {
            updateRoomGrid(data.roomTypes, data.roomTypeImages);
        } else {
            console.error(data);
        }
    } catch (err) {
        console.error("Lỗi kết nối:", err);
    }
}

// Hàm vẽ lại danh sách phòng khi có dữ liệu mới
function updateRoomGrid(roomTypes, roomTypeImages) {
    const grid = document.querySelector('.roomtypes-grid');
    grid.innerHTML = ''; // Xóa sạch danh sách cũ

    roomTypes.forEach(rt => {
        const images = roomTypeImages[rt.roomTypeId] || [];
        const imgSrc = images.length > 0 ? images[0].imageUrl : 'data:image/svg+xml...';
        
        const html = `
            <div class="roomtype-card">
                <img src="${imgSrc}" class="roomtype-image" alt="Room">
                <div class="roomtype-body">
                    <h3 class="roomtype-name">${rt.name}</h3>
                    <p class="roomtype-description">${rt.description}</p>
                    <div class="roomtype-price">${rt.price.toLocaleString()} VNĐ/night</div>
                    <div class="available-info">Còn trống: ${rt.availableRooms} phòng</div>
                    <div class="roomtype-actions">
                         <button class="btn-book" ${rt.availableRooms === 0 ? 'disabled' : ''} onclick="handleBooking(${rt.roomTypeId})">📅 Book Now</button>
                    </div>
                </div>
            </div>`;
        grid.insertAdjacentHTML('beforeend', html);
    });
}

filterBtn.addEventListener('click', filter);

function handleBooking(roomTypeId) {

}