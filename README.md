# 🎮 RPG Booking System

Hệ thống đặt lịch phòng chơi game nhập vai (RPG) trực tuyến, tích hợp tính năng kiểm tra lịch trống tự động, quản lý người dùng và thanh toán.

## 🚀 Tính năng chính

* **Đặt lịch thông minh:** Kiểm tra thời gian trống theo thời gian thực (real-time check).
* **Xử lý xung đột:** Tự động phát hiện trùng lịch và chặn các yêu cầu đặt phòng đè lên lịch đã xác nhận (`CONFIRMED`).
* **Trải nghiệm người dùng (UX):** * Tự động khóa các khung giờ đã được đặt với hiệu ứng gạch ngang.
    * Tự động cập nhật đơn hàng nếu khách hàng quay lại (Back) và chỉnh sửa thông tin.
* **Khách vãng lai (Guest Booking):** Tự động tạo tài khoản, gửi thông tin đăng nhập qua Email và đăng nhập tự động.
* **Bảo mật:** Sử dụng Spring Security để quản lý phiên làm việc và xác thực.

## 🛠 Công nghệ sử dụng

* **Backend:** Java(17), Spring Boot(3.x) (Web, Security, Data JPA, Mail)
* **Frontend:** Thymeleaf, Bootstrap 5, JavaScript (Fetch API)
* **Database:** MySQL
* **Thư viện:** Lombok, Jakarta Servlet

## 📋 Cấu trúc dự án

* `/controller`: Xử lý điều hướng và request từ người dùng.
* `/service`: Chứa logic nghiệp vụ (tính toán giờ chơi, xử lý trùng lịch, gửi mail).
* `/repository`: Giao tiếp với cơ sở dữ liệu.
* `/model`: Các thực thể dữ liệu (Booking, Room, User).

## ⚙️ Cấu hình

1.  **Cơ sở dữ liệu:** Cấu hình thông tin kết nối trong `application.properties`:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/rpg_booking
    spring.datasource.username=root
    spring.datasource.password=your_password
    ```
2.  **Email:** Cấu hình SMTP để gửi thông tin tài khoản cho khách:
    ```properties
    spring.mail.host=smtp.gmail.com
    spring.mail.port=587
    spring.mail.username=your-email@gmail.com
    spring.mail.password=your-app-password
    ```

## 💡 Luồng hoạt động



1.  Người dùng chọn Phòng và Ngày.
2.  Hệ thống gọi API để lấy danh sách giờ đã đặt.
3.  Người dùng chọn khung giờ trống và điền thông tin.
4.  Hệ thống validate (Tổng số người chơi, Trùng lịch).
5.  Xác nhận và chuyển hướng thanh toán.

## 🤝 Hướng dẫn phát triển

* `BookingService.java`: Nơi điều chỉnh logic tính toán thời gian `startTime` và `endTime`.
* `booking.js`: Nơi điều chỉnh giao diện hiển thị giờ đặt phòng.

Run project -> Run file RpgBookingApplication