package com.example.RpgBooking.controller.admin;

import com.example.RpgBooking.model.Contact;
import com.example.RpgBooking.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/contacts")
public class AdminContactController {

    @Autowired
    private ContactRepository contactRepo;

    @GetMapping
    public String index(Model model,
                        @RequestParam(value = "keyword", required = false) String keyword,
                        @PageableDefault(size = 15) Pageable pageable) {
        Page<Contact> contactPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String cleanKeyword = keyword.trim();
            contactPage = contactRepo.searchByKeyword(cleanKeyword, pageable);
            model.addAttribute("keyword", cleanKeyword);
        } else {
            contactPage = contactRepo.findAll(pageable);
        }

        model.addAttribute("contactPage", contactPage);
        return "admin/contacts/index";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Contact contact = contactRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy liên hệ ID: " + id));

        contact.setActive(!contact.isActive());
        contactRepo.save(contact);

        redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái liên hệ thành công!");
        return "redirect:/admin/contacts";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        contactRepo.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Xóa liên hệ thành công!");
        return "redirect:/admin/contacts";
    }
}
