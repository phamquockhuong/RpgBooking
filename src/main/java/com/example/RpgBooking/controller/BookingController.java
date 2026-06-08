package com.example.RpgBooking.controller;

import com.example.RpgBooking.dto.BookingRequest;
import com.example.RpgBooking.model.Room;
import com.example.RpgBooking.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class BookingController {

    private final RoomService roomService;
    private final CategoryService categoryService;

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
}