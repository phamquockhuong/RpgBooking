package com.example.RpgBooking.controller.admin;

import com.example.RpgBooking.dto.TopRoomDTO;
import com.example.RpgBooking.repository.BookingRepository;
import com.example.RpgBooking.repository.EventRepository;
import com.example.RpgBooking.repository.RoomRepository;
import com.example.RpgBooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    @Autowired
    private BookingRepository bookingRepo;
    private RoomRepository roomRepo;
    private UserRepository userRepo;
    private EventRepository eventRepo;

    public DashboardController(RoomRepository roomRepo, UserRepository userRepo, EventRepository eventRepo) {
        this.roomRepo = roomRepo;
        this.userRepo = userRepo;
        this.eventRepo = eventRepo;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);

        List<TopRoomDTO> topRooms = bookingRepo.findTopRooms(firstDayOfMonth);

        model.addAttribute("topRooms", topRooms);

        model.addAttribute("totalBookings", bookingRepo.count());

        model.addAttribute("totalUsers", userRepo.count());

        model.addAttribute("totalRooms", roomRepo.count());

        model.addAttribute("totalEvents", eventRepo.count());

        return "admin/dashboard";
    }
}