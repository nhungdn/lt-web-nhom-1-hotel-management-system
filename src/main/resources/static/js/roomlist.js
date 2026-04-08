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
            clone.querySelector(".checkin").textContent = room.checkIn || "-";
            clone.querySelector(".checkout").textContent = room.checkOut || "-";
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
