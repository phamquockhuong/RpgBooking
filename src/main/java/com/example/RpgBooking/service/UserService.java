package com.example.RpgBooking.service;

import com.example.RpgBooking.model.User;
import com.example.RpgBooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;

    public void resetPassword(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Email không tồn tại trong hệ thống!"));

        String newPassword = UUID.randomUUID().toString().substring(0, 8);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        sendEmail(email, "Mật khẩu mới của bạn",
                "Mật khẩu mới của bạn là: " + newPassword + "\n\nVui lòng đăng nhập và đổi lại mật khẩu ngay.");
    }

    private void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + username));
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return false;
        }

        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }

        return false;
    }
}
