document.addEventListener("DOMContentLoaded", function() {
    const dateInput = document.getElementById('bookingDate');
    const durationInput = document.getElementById('roomDuration');
    const roomIdInput = document.getElementById('roomId');

    if (!dateInput || !durationInput || !roomIdInput) return;

    const today = new Date();
    const thirtyDaysLater = new Date();
    thirtyDaysLater.setDate(today.getDate() + 30);
    const formatDate = (date) => date.toISOString().split('T')[0];

    dateInput.min = formatDate(today);
    dateInput.max = formatDate(thirtyDaysLater);
    if (!dateInput.value) dateInput.value = formatDate(today);

    const roomDuration = parseInt(durationInput.value);
    const roomId = roomIdInput.value;

    loadAndPopulateSlots(roomId, dateInput.value, roomDuration);

    dateInput.addEventListener('change', function() {
        loadAndPopulateSlots(roomId, this.value, roomDuration);
    });
});

function loadAndPopulateSlots(roomId, selectedDate, durationInMinutes) {
    fetch(`/api/bookings/confirmed?roomId=${roomId}&date=${selectedDate}`)
        .then(response => response.json())
        .then(confirmedBookings => {
            populateTimeSlots(durationInMinutes, confirmedBookings);
        })
        .catch(error => {
            console.error("Lỗi khi tải lịch đặt phòng:", error);
        });
}

function formatTime(totalMinutes) {
    let hours = Math.floor(totalMinutes / 60);
    let minutes = totalMinutes % 60;
    return String(hours).padStart(2, '0') + ":" + String(minutes).padStart(2, '0');
}

function timeToMinutes(timeStr) {
    if (!timeStr) return 0;
    const parts = timeStr.split(':');
    return parseInt(parts[0]) * 60 + parseInt(parts[1]);
}

function populateTimeSlots(durationInMinutes, confirmedBookings) {
    const select = document.getElementById('startTimeSelect');
    const dateInput = document.getElementById('bookingDate');

    if (!select) return;

    select.innerHTML = '';

    const now = new Date();

    const selectedDate = dateInput.value;
    const today = now.toISOString().split('T')[0];

    const currentMinutes =
        now.getHours() * 60 + now.getMinutes();

    for (let h = 8; h < 22; h++) {

        let startMinutes = h * 60;
        let endMinutes = startMinutes + durationInMinutes;

        if (endMinutes <= 22 * 60) {

            let startStr = formatTime(startMinutes);
            let endStr = formatTime(endMinutes);

            let option = document.createElement('option');
            option.value = startStr;

            let isPastTime =
                selectedDate === today &&
                startMinutes <= currentMinutes;

            let isAlreadyBooked = confirmedBookings.some(b => {
                let bookedStart = timeToMinutes(b.startTime);
                let bookedEnd = timeToMinutes(b.endTime);

                return (
                    startMinutes < bookedEnd &&
                    endMinutes > bookedStart
                );
            });

            if (isPastTime) {
                option.text = "⏰ " + startStr + " - " + endStr;
                option.disabled = true;
                option.style.textDecoration = "line-through";
                option.style.color = "#a0a0a0";
            }
            else if (isAlreadyBooked) {
                option.text = "❌ " + startStr + " - " + endStr;
                option.disabled = true;
                option.style.textDecoration = "line-through";
                option.style.color = "#a0a0a0";
            }
            else {
                option.text = startStr + " - " + endStr;
            }

            select.add(option);
        }
    }
}