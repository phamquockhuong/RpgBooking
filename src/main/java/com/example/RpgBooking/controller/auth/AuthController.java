package com.example.RpgBooking.controller.auth;

import com.example.RpgBooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.Model;
import com.example.RpgBooking.model.User;
import com.example.RpgBooking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private JavaMailSender mailSender;

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/auth/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/auth/register")
    public String register(@ModelAttribute User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole("ROLE_USER");
        userRepository.save(user);
        sendRegistrationEmail(user.getEmail(), user.getUsername());

        return "redirect:/login?success";
    }

    private void sendRegistrationEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Chào mừng đến với hệ thống RPG Booking!");
        message.setText("Xin chào " + username + ",\n\nChúc mừng bạn đã đăng ký tài khoản thành công tại RPG Booking. Hãy đặt phòng ngay để tận hưởng ưu đãi!");
        mailSender.send(message);
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        try {
            userService.resetPassword(email);
            model.addAttribute("message", "Mật khẩu mới đã được gửi vào email của bạn.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "auth/forgot-password";
    }
}
