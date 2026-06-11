package com.example.RpgBooking.controller.admin;

import com.example.RpgBooking.model.Event;
import com.example.RpgBooking.repository.EventRepository;
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
@RequestMapping("/admin/events")
public class EventController {

    @Autowired
    private EventRepository eventRepo;

    @GetMapping
    public String index(Model model,
                        @PageableDefault(size = 15) Pageable pageable) {

        Page<Event> eventPage = eventRepo.findAll(pageable);

        model.addAttribute("eventPage", eventPage);

        return "admin/events/index";
    }

    @GetMapping("/add")
    public String addForm(Model model) {

        model.addAttribute("event", new Event());

        return "admin/events/add";
    }

    @PostMapping("/add")
    public String saveAdd(
            @Valid @ModelAttribute Event event,
            BindingResult result,
            Model model) {

        if (eventRepo.existsByName(event.getName())) {
            result.rejectValue(
                    "name",
                    "error.event",
                    "Tên sự kiện đã tồn tại"
            );
        }

        if (event.getStartDate() != null
                && event.getEndDate() != null
                && event.getStartDate().isAfter(event.getEndDate())) {

            result.rejectValue(
                    "endDate",
                    "error.event",
                    "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu"
            );
        }

        if (result.hasErrors()) {
            return "admin/events/add";
        }

        eventRepo.save(event);

        return "redirect:/admin/events";
    }

    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            Model model) {

        Event event = eventRepo.findById(id)
                .orElseThrow();

        model.addAttribute("event", event);

        return "admin/events/edit";
    }

    @PostMapping("/edit/{id}")
    public String saveEdit(
            @PathVariable Long id,
            @Valid @ModelAttribute Event event,
            BindingResult result) {

        if (eventRepo.existsByNameAndIdNot(event.getName(), id)) {

            result.rejectValue(
                    "name",
                    "error.event",
                    "Tên sự kiện đã tồn tại"
            );
        }

        if (event.getStartDate() != null
                && event.getEndDate() != null
                && event.getStartDate().isAfter(event.getEndDate())) {

            result.rejectValue(
                    "endDate",
                    "error.event",
                    "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu"
            );
        }

        if (result.hasErrors()) {
            return "admin/events/edit";
        }

        event.setId(id);

        eventRepo.save(event);

        return "redirect:/admin/events";
    }

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        eventRepo.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "success",
                "Xóa sự kiện thành công"
        );

        return "redirect:/admin/events";
    }
}