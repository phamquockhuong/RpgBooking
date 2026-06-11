package com.example.RpgBooking.controller.admin;

import com.example.RpgBooking.model.Faq;
import com.example.RpgBooking.repository.FaqRepository;
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
@RequestMapping("/admin/faqs")
public class FaqController {

    @Autowired
    private FaqRepository faqRepo;

    @GetMapping
    public String index(Model model,
                        @RequestParam(value = "keyword", required = false) String keyword,
                        @PageableDefault(size = 15) Pageable pageable) {

        Page<Faq> faqPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            faqPage = faqRepo.findByQuestionContainingIgnoreCase(keyword.trim(), pageable);
            model.addAttribute("keyword", keyword.trim());
        } else {
            faqPage = faqRepo.findAll(pageable);
        }

        model.addAttribute("faqPage", faqPage);
        return "admin/faqs/index";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("faq", new Faq());
        return "admin/faqs/add";
    }

    @PostMapping("/add")
    public String saveAdd(
            @Valid @ModelAttribute("faq") Faq faq,
            BindingResult result,
            Model model) {

        if (faqRepo.existsByQuestion(faq.getQuestion())) {
            result.rejectValue(
                    "question",
                    "error.faq",
                    "Câu hỏi này đã tồn tại trong hệ thống"
            );
        }

        if (result.hasErrors()) {
            return "admin/faqs/add";
        }

        faqRepo.save(faq);
        return "redirect:/admin/faqs";
    }

    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            Model model) {

        Faq faq = faqRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy FAQ với ID: " + id));

        model.addAttribute("faq", faq);
        return "admin/faqs/edit";
    }

    @PostMapping("/edit/{id}")
    public String saveEdit(
            @PathVariable Long id,
            @Valid @ModelAttribute("faq") Faq faq,
            BindingResult result) {

        if (faqRepo.existsByQuestionAndIdNot(faq.getQuestion(), id)) {
            result.rejectValue(
                    "question",
                    "error.faq",
                    "Câu hỏi này đã tồn tại trong hệ thống"
            );
        }

        if (result.hasErrors()) {
            return "admin/faqs/edit";
        }

        faq.setId(id);
        faqRepo.save(faq);

        return "redirect:/admin/faqs";
    }

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        faqRepo.deleteById(id);
        redirectAttributes.addFlashAttribute(
                "success",
                "Xóa câu hỏi thành công"
        );

        return "redirect:/admin/faqs";
    }
}