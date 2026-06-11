package com.example.RpgBooking.controller.admin;

import com.example.RpgBooking.model.Room;
import com.example.RpgBooking.repository.CategoryRepository;
import com.example.RpgBooking.repository.RoomRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/admin/rooms")
public class RoomController {

    @Autowired private RoomRepository roomRepo;
    @Autowired private CategoryRepository catRepo;

    @GetMapping
    public String list(Model model,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @PageableDefault(size = 15) Pageable pageable) {

        Page<Room> roomPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String cleanKeyword = keyword.trim();
            roomPage = roomRepo.searchByText(cleanKeyword, pageable);

            model.addAttribute("keyword", cleanKeyword);
        } else {
            roomPage = roomRepo.findAll(pageable);
        }

        model.addAttribute("roomPage", roomPage);
        return "admin/rooms/index";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("room", new Room());
        model.addAttribute("categories", catRepo.findAll());
        return "admin/rooms/add";
    }

    @PostMapping("/add")
    public String saveAdd(@Valid @ModelAttribute Room room, BindingResult result,
                          @RequestParam("imageFile") MultipartFile file, Model model) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute("categories", catRepo.findAll());
            return "admin/rooms/add";
        }

        if (!file.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), Paths.get("src/main/resources/static/uploads/" + fileName));
            room.setImageUrl("/uploads/" + fileName);
        }

        roomRepo.save(room);
        return "redirect:/admin/rooms";
    }

    // Controller cho Edit
    @PostMapping("/edit/{id}")
    public String saveEdit(@PathVariable Long id, @Valid @ModelAttribute Room room, BindingResult result,
                           @RequestParam("imageFile") MultipartFile file, Model model) throws Exception {

        if (result.hasErrors()) {
            model.addAttribute("categories", catRepo.findAll());
            return "admin/rooms/edit";
        }

        Room oldRoom = roomRepo.findById(id).orElseThrow(() -> new Exception("Phòng không tồn tại"));
        room.setId(id);

        if (!file.isEmpty()) {
            if (oldRoom.getImageUrl() != null) {
                Path oldPath = Paths.get("src/main/resources/static" + oldRoom.getImageUrl());
                Files.deleteIfExists(oldPath);
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), Paths.get("src/main/resources/static/uploads/" + fileName));
            room.setImageUrl("/uploads/" + fileName);
        } else {
            room.setImageUrl(oldRoom.getImageUrl());
        }

        roomRepo.save(room);
        return "redirect:/admin/rooms";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("room", roomRepo.findById(id).orElseThrow());
        model.addAttribute("categories", catRepo.findAll());
        return "admin/rooms/edit";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Room room = roomRepo.findById(id).orElse(null);

        if (room == null) {
            redirectAttributes.addFlashAttribute("error", "Phòng không tồn tại!");
            return "redirect:/admin/rooms";
        }

        try {
            if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
                Path path = Paths.get("src/main/resources/static" + room.getImageUrl());
                Files.deleteIfExists(path);
            }

            roomRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa phòng và ảnh thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa phòng này (có thể do đang có booking liên quan)!");
        }

        return "redirect:/admin/rooms";
    }
}