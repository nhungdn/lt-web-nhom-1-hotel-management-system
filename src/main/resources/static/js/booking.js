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
    
    const roomRow = document.querySelectorAll("tbody tr");
    const detailPopup = document.querySelector(".detail-overlay");
    const closeBtn = document.querySelectorAll(".close-btn");
    roomRow.forEach((row)=>{
        row.addEventListener('click', () => {
            console.log('Row clicked!');
            detailPopup.style.display = "block";
        });
    });
    
    closeBtn.addEventListener("click", () => {
        detailPopup.classList.add("hidden");
    });
    
});

document.querySelector('.booking-form').addEventListener('submit', function(e) {
    e.preventDefault();

    // Create success message
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

    // Reset form
    this.reset();
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