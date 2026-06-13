package com.example.RpgBooking.controller;

import com.example.RpgBooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {
    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", userService.findByUsername(principal.getUsername()));
        return "user/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
                                 @RequestParam String oldPassword, @RequestParam String newPassword,
                                 RedirectAttributes ra) {
        if (principal == null) {
            return "redirect:/login";
        }

        if (userService.changePassword(principal.getUsername(), oldPassword, newPassword)) {
            ra.addFlashAttribute("message", "Đổi mật khẩu thành công!");
        } else {
            ra.addFlashAttribute("error", "Mật khẩu cũ không đúng!");
        }
        return "redirect:/profile";
    }
}
