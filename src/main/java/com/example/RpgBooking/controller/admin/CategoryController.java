package com.example.RpgBooking.controller.admin;

import com.example.RpgBooking.model.Category;
import com.example.RpgBooking.model.Room;
import com.example.RpgBooking.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String list(Model model, @PageableDefault(size = 15) Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);

        model.addAttribute("categories", categories);
        return "admin/categories/index";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        if (categoryRepository.existsByName(category.getName())) {
            redirectAttributes.addFlashAttribute("error", "Danh mục này đã tồn tại!");
            return "redirect:/admin/categories";
        }

        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("message", "Thêm thành công!");
        return "redirect:/admin/categories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa danh mục đang được sử dụng!");
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryRepository.findById(id).orElseThrow());
        return "admin/categories/edit";
    }

    @PostMapping("/edit")
    public String update(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        if (categoryRepository.existsByNameAndIdNot(category.getName(), category.getId())) {
            redirectAttributes.addFlashAttribute("error", "Tên danh mục này đã tồn tại ở mục khác!");
            return "redirect:/admin/categories";
        }

        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("message", "Cập nhật thành công!");
        return "redirect:/admin/categories";
    }
}
