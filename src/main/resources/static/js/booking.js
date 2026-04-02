document.addEventListener("DOMContentLoaded", () => {
    const closeBtn = document.querySelectorAll(".close-btn");
    const enterBookingBtn = document.querySelector(".booking-btn");
    const roomRow = document.querySelectorAll("tbody tr btn");
    const overlayDiv = document.querySelector(".detail-overlay");
    const bookingDiv = document.querySelector(".booking-div");
    console.log("Booking script has run.");
    if (enterBookingBtn) {
        enterBookingBtn.addEventListener("click", () => {
            console.log("Open form!"); 
            if(bookingDiv.style.display === "flex")
                bookingDiv.style.display = "none";
            else
                bookingDiv.style.display = "flex";
        });
    }
    
    roomRow.forEach((row)=>{
        row.addEventListener('click', () => {
            console.log('Row clicked!');
            overlayDiv.style.display = "block";
        });
    });
    
    if (closeBtn) {
        closeBtn.addEventListener("click", () => {
            overlayDiv.style.display = "none";
            bookingDiv.style.display = "none";
        });
    }
});

document.querySelector('form').addEventListener('submit', function(e) {
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