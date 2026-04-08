document.addEventListener("DOMContentLoaded", () => {
    function getRoomList(){
        console.log("RL function has run.");
        const roomTable = document.getElementById("roomTable");
        const roomTbody = roomTable.querySelector("tbody");
        const rooms = JSON.parse(roomTable.getAttribute("rooms-list"));
        console.log(rooms);
        for(const room of rooms){
            const temp = document.querySelector("#tr-template");
            const clone = temp.content.cloneNode(true);
            
            clone.querySelector(".table-room-name").textContent = room.roomTypeName;
            clone.querySelector(".table-room-desc").textContent = room.roomDesc;
            clone.querySelector(".room-id").textContent = room.roomNumber;
            const stat = clone.querySelector(".status-badge");
            stat.textContent = room.status.trim();
            switch(room.status){
                case "AVAILABLE":
                    stat.classList.add("green");
                    break;
                case "BOOKED":
                    stat.classList.add("yellow");
                    break;
                case "OCCUPIED":
                    stat.classList.add("red");
                    break;
                case "CLEANING":
                    stat.classList.add("blue");
                    break;
            }
            clone.querySelector(".checkin").innerHTML = formatDateTime(room.checkIn) || "-";
            clone.querySelector(".checkout").innerHTML = formatDateTime(room.checkOut) || "-";
            clone.querySelector(".price").textContent = room.price;
            clone.querySelector("input").setAttribute('id', room.roomNumber+"Check");
            clone.querySelector(".roomtab button").setAttribute('id', room.roomNumber+"EditRoomBtn");
            clone.querySelector(".booktab button").setAttribute('id', room.roomNumber+"EditBookBtn");
            roomTbody.appendChild(clone);
        }
    }
    console.log("Roomlist script has run.");
    getRoomList();

});

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

    // Format giờ:phút
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${dayName}, ${day}/${month}/${year} <br/> ${hours}:${minutes}`;
}
