package com.example.RpgBooking.controller.admin;

import com.example.RpgBooking.model.Event;
import com.example.RpgBooking.model.User;
import com.example.RpgBooking.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String index(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable,
            Model model
    ) {

        Page<User> users;

        if (keyword != null && !keyword.trim().isEmpty()) {

            users = userRepo.findByUsernameContainingIgnoreCase(
                    keyword,
                    pageable
            );

        } else {

            users = userRepo.findAll(pageable);

        }

        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);

        return "admin/users/index";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/users/add";
    }

    @PostMapping("/add")
    public String saveAdd(
            @Valid @ModelAttribute User user,
            BindingResult result,
            Model model
    ) {

        if (userRepo.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "", "Username đã tồn tại");
        }

        if (userRepo.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "", "Email đã tồn tại");
        }

        if (result.hasErrors()) {
            return "admin/users/add";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepo.save(user);

        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {

        User user = userRepo.findById(id)
                .orElseThrow();

        model.addAttribute("user", user);

        return "admin/users/edit";
    }

    @PostMapping("/edit/{id}")
    public String saveEdit(
            @PathVariable Long id,
            @ModelAttribute User user,
            BindingResult result
    ) {

        User existing = userRepo.findById(id)
                .orElseThrow();

        if (!existing.getUsername().equals(user.getUsername())
                && userRepo.existsByUsername(user.getUsername())) {

            result.rejectValue("username", "", "Username đã tồn tại");
        }

        if (!existing.getEmail().equals(user.getEmail())
                && userRepo.existsByEmail(user.getEmail())) {

            result.rejectValue("email", "", "Email đã tồn tại");
        }

        if (result.hasErrors()) {
            return "admin/users/edit";
        }

        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        existing.setRole(user.getRole());

        if (user.getPassword() != null &&
                !user.getPassword().isBlank()) {

            existing.setPassword(
                    passwordEncoder.encode(user.getPassword())
            );
        }

        userRepo.save(existing);

        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes ra
    ) {

        userRepo.deleteById(id);

        ra.addFlashAttribute("success",
                "Xóa người dùng thành công!");

        return "redirect:/admin/users";
    }
}