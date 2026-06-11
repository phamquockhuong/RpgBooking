package com.example.RpgBooking.controller;

import com.example.RpgBooking.dto.BookingRequest;
import com.example.RpgBooking.model.Booking;
import com.example.RpgBooking.model.Contact;
import com.example.RpgBooking.model.Room;
import com.example.RpgBooking.repository.CategoryRepository;
import com.example.RpgBooking.repository.ContactRepository;
import com.example.RpgBooking.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class BookingController {

    private final RoomService roomService;
    private final CategoryService categoryService;
    private final BookingService bookingService;
    private final CouponService couponService;
    private final FaqService faqService;

    @Autowired
    private ContactRepository contactRepo;

    @GetMapping("/")
    public String bookingsPage(
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 6) Pageable pageable,
            Model model) {

        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("topRooms", roomService.getTopBookedRooms());

        Page<Room> roomPage;
        if (categoryId != null) {
            roomPage = roomService.getRoomsByCategoryId(categoryId, pageable);
        } else {
            roomPage = roomService.getActiveRooms(pageable);
        }

        model.addAttribute("roomPage", roomPage);
        model.addAttribute("selectedCategoryId", categoryId);
        return "user/bookings";
    }

    @GetMapping("/booking/{id}")
    public String showBookingPage(@PathVariable Long id, Model model) {
        try {
            Room room = roomService.getRoomById(id);
            model.addAttribute("room", room);
            model.addAttribute("bookingRequest", new BookingRequest());
            return "user/booking-detail";
        } catch (RuntimeException e) {
            return "redirect:/";
        }
    }

    @PostMapping("/booking/create")
    public String create(BookingRequest req,
                         @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes) {
        try {
            Booking booking = bookingService.createPendingBooking(req, principal, request);
            return "redirect:/booking/" + booking.getId() + "/payment";

        } catch (RuntimeException e) {
            String friendlyMessage = "Có lỗi xảy ra!";
            if (e.getMessage().contains("Time slot already booked")) {
                friendlyMessage = "Khung giờ này vừa có khách khác đặt mất rồi! Vui lòng chọn giờ hoặc ngày khác.";
            } else if (e.getMessage().contains("You already have this booking")) {
                friendlyMessage = "Bạn đang có một đơn đặt phòng tương tự đang chờ thanh toán. Vui lòng kiểm tra lại đơn hàng!";
            } else {
                friendlyMessage = e.getMessage();
            }

            redirectAttributes.addFlashAttribute("errorMessage", friendlyMessage);

            return "redirect:/booking/" + req.getRoomId();
        }
    }

    @GetMapping("/booking/{id}/payment")
    public String payment(@PathVariable Long id, Model model) {

        Booking booking = bookingService.getById(id);

        double price =
                booking.getNumAdult() * booking.getRoom().getPriceAdult()
                        + booking.getNumKid() * booking.getRoom().getPriceKid();

        double tax = price * 0.1;

        model.addAttribute("booking", booking);
        model.addAttribute("price", price);
        model.addAttribute("tax", tax);
        model.addAttribute("discount", 0);
        model.addAttribute("total", price + tax);

        return "user/payment";
    }

    @GetMapping("/about-us")
    public String aboutUsPage() { return "user/about-us"; }

    @GetMapping("/faq")
    public String faqPage(Model model) {

        model.addAttribute(
                "faqs",
                faqService.getActiveFaqs()
        );

        return "user/faq";
    }

    @GetMapping("/contact")
    public String contactPage() { return "user/contact"; }

    @PostMapping("/contact")
    public String handleSendContact(@ModelAttribute Contact contact,
                                    RedirectAttributes redirectAttributes) {

        contact.setActive(false);

        contactRepo.save(contact);

        redirectAttributes.addFlashAttribute("successMessage", "Gửi yêu cầu liên hệ thành công! Chúng tôi sẽ phản hồi sớm nhất.");

        return "redirect:/contact";
    }

    @ModelAttribute("currentPath")
    public String getCurrentPath(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/api/bookings/confirmed")
    @ResponseBody
    public List<Booking> getConfirmedBookingsByDate(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return bookingService.getConfirmedBookingsByRoomAndDate(roomId, date);
    }

    @PostMapping("/booking/apply-coupon")
    @ResponseBody
    public Map<String, Object> applyCoupon(
            @RequestParam Long bookingId,
            @RequestParam String couponCode) {

        Booking booking = bookingService.getById(bookingId);

        return couponService.applyCoupon(booking, couponCode);
    }

    @PostMapping("/booking/confirm-payment")
    public String confirmPayment(@RequestParam Long bookingId) {

        bookingService.confirmPayment(bookingId);

        return "redirect:/booking/success";
    }

    @GetMapping("/booking/success")
    public String success() {
        return "user/booking-success";
    }
}