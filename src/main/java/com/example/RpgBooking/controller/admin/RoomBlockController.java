package com.example.RpgBooking.controller.admin;

import com.example.RpgBooking.model.Room;
import com.example.RpgBooking.model.RoomBlock;
import com.example.RpgBooking.repository.RoomBlockRepository;
import com.example.RpgBooking.repository.RoomRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/room-blocks")
public class RoomBlockController {

    @Autowired private RoomBlockRepository roomBlockRepo;
    @Autowired private RoomRepository roomRepo;

    @GetMapping
    public String list(Model model,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @PageableDefault(size = 15) Pageable pageable) {

        Page<RoomBlock> roomPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String cleanKeyword = keyword.trim();
            roomPage = roomBlockRepo.searchBookings(cleanKeyword, pageable);

            model.addAttribute("keyword", cleanKeyword);
        } else {
            roomPage = roomBlockRepo.findAll(pageable);
        }

        model.addAttribute("roomPage", roomPage);
        return "admin/room_blocks/index";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("roomBlock", new RoomBlock());
        model.addAttribute("rooms", roomRepo.findByActiveTrue());
        return "admin/room_blocks/add";
    }

    @PostMapping("/add")
    public String saveAdd(@Valid @ModelAttribute RoomBlock roomBlock,
                          BindingResult result,
                          Model model) {

        if (roomBlock.getStartTime().isAfter(roomBlock.getEndTime())) {
            result.rejectValue(
                    "endTime",
                    "error.endTime",
                    "Thời gian kết thúc phải lớn hơn thời gian bắt đầu"
            );
        }

        if (roomBlockRepo.existsByRoomIdAndStartTimeAndEndTime(
                roomBlock.getRoom().getId(),
                roomBlock.getStartTime(),
                roomBlock.getEndTime()
        )) {
            result.reject(
                    "duplicate",
                    "Phòng này đã có lịch chặn trong khoảng thời gian này"
            );
        }

        if (result.hasErrors()) {
            model.addAttribute("rooms", roomRepo.findByActiveTrue());
            return "admin/room_blocks/add";
        }

        roomBlockRepo.save(roomBlock);
        return "redirect:/admin/room-blocks";
    }

    @PostMapping("/edit/{id}")
    public String saveEdit(@PathVariable Long id,
                           @Valid @ModelAttribute RoomBlock roomBlock,
                           BindingResult result,
                           Model model) {

        if (roomBlock.getStartTime().isAfter(roomBlock.getEndTime())) {
            result.rejectValue(
                    "endTime",
                    "error.endTime",
                    "Thời gian kết thúc phải lớn hơn thời gian bắt đầu"
            );
        }

        if (roomBlockRepo.existsByRoomIdAndStartTimeAndEndTimeAndIdNot(
                roomBlock.getRoom().getId(),
                roomBlock.getStartTime(),
                roomBlock.getEndTime(),
                id
        )) {
            result.reject(
                    "duplicate",
                    "Phòng này đã có lịch chặn trong khoảng thời gian này"
            );
        }

        if (result.hasErrors()) {
            model.addAttribute("rooms", roomRepo.findByActiveTrue());
            return "admin/room_blocks/edit";
        }

        roomBlock.setId(id);

        roomBlockRepo.save(roomBlock);

        return "redirect:/admin/room-blocks";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("roomBlock", roomBlockRepo.findById(id).orElseThrow());
        model.addAttribute("rooms", roomRepo.findByActiveTrue());
        return "admin/room_blocks/edit";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {

        RoomBlock roomBlock = roomBlockRepo.findById(id).orElse(null);

        if (roomBlock == null) {
            redirectAttributes.addFlashAttribute("error", "Block không tồn tại!");
            return "redirect:/admin/room-blocks";
        }

        roomBlockRepo.delete(roomBlock);

        redirectAttributes.addFlashAttribute("success", "Xóa thành công!");
        return "redirect:/admin/room-blocks";
    }
}