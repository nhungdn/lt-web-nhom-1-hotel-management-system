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
function viewRoomTypeDetails(btn) {
    const roomTypeId = btn.getAttribute('data-id');
    const roomTypeName = btn.getAttribute('data-name');
    const description = btn.getAttribute('data-description');

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

const openCustomerFormBtn = document.querySelector('#custInfo');
const customerForm = document.querySelector('.filter-date-container .dropdown-form');
openCustomerFormBtn.addEventListener("click", () => {
    customerForm.classList.toggle('hidden');
})

const closeCustomerFormBtn = customerForm.querySelector('.close-btn');
closeCustomerFormBtn.addEventListener("click", () => {
    customerForm.classList.add('hidden');
})