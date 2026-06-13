document.addEventListener("DOMContentLoaded", function () {

    const btn = document.getElementById("btnApplyCoupon");

    if (!btn) return;

    btn.addEventListener("click", function () {

        const bookingId =
        document.getElementById("bookingId").value;

        const couponCode =
        document.getElementById("couponCode").value;

        fetch("/booking/apply-coupon", {
            method: "POST",
            headers: {
            "Content-Type":
            "application/x-www-form-urlencoded"
        },
            body:
            "bookingId=" + bookingId +
            "&couponCode=" + couponCode
        })
            .then(r => r.json())
            .then(data => {

            if (!data.success) {
                alert(data.message);
                return;
            }
                console.log(data)
            document.getElementById("price").innerText = formatVND(data.price);

            document.getElementById("tax").innerText = formatVND(data.tax);

            document.getElementById("discount").innerText = formatVND(data.discount);

            document.getElementById("total").innerText = formatVND(data.total);
        });

    });

});

function formatVND(amount) {
    return new Intl.NumberFormat('vi-VN').format(amount) + ' ₫';
}
