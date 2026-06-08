document.addEventListener("DOMContentLoaded", function() {
    const dateInput = document.getElementById('bookingDate');

    if (!dateInput) return;

    const today = new Date();
    const thirtyDaysLater = new Date();
    thirtyDaysLater.setDate(today.getDate() + 30);

    const formatDate = (date) => date.toISOString().split('T')[0];
    dateInput.min = formatDate(today);
    dateInput.max = formatDate(thirtyDaysLater);

    dateInput.value = formatDate(today);

    const roomDuration = parseInt(document.getElementById('roomDuration').value);
    populateTimeSlots(roomDuration);
});

function formatTime(totalMinutes) {
    let hours = Math.floor(totalMinutes / 60);
    let minutes = totalMinutes % 60;
    return String(hours).padStart(2, '0') + ":" + String(minutes).padStart(2, '0');
}

function populateTimeSlots(durationInMinutes) {
    const select = document.getElementById('startTimeSelect');
    select.innerHTML = '';

    for (let h = 8; h < 22; h++) {
        let startMinutes = h * 60;
        let endMinutes = startMinutes + durationInMinutes;

        if (endMinutes <= 22 * 60) {
            let startStr = formatTime(startMinutes);
            let endStr = formatTime(endMinutes);

            let option = document.createElement('option');
            option.value = startStr;
            option.text = startStr + " - " + endStr;
            select.add(option);
        }
    }

    calculateTotal();
}

function calculateTotal() {
    const priceAdult = parseFloat(document.getElementById('priceAdult').innerText) || 0;
    const priceKid = parseFloat(document.getElementById('priceKid').innerText) || 0;

    const numAdult = parseInt(document.getElementById('numAdult').value) || 1;
    const numKid = parseInt(document.getElementById('numKid').value) || 1;

    const discount = parseFloat(document.getElementById('discountAmount').innerText.replace(/\./g, '')) || 0;

    const base = (numAdult * priceAdult) + (numKid * priceKid);
    const gst = base * 0.1;
    const total = base + gst - discount;

    document.getElementById('basePrice').innerText = base.toLocaleString('vi-VN');
    document.getElementById('gstAmount').innerText = gst.toLocaleString('vi-VN');
    document.getElementById('finalTotal').innerText = total.toLocaleString('vi-VN');
}

function applyVoucher() {
    alert("Đã áp dụng voucher thành công!");
    document.getElementById('discountAmount').innerText = "50.000";
    calculateTotal();
}