package com.musiclogin.service;

import com.musiclogin.dao.UserRepository;
import com.musiclogin.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户业务逻辑层
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    /**
     * 登录
     * @return 成功返回 "ok"，失败返回错误消息
     */
    public String login(String username, String password, HttpSession session) {
        User user = userRepository.findByUsernameAndPassword(username, password);
        if (user == null) {
            return "用户名或密码不正确";
        }
        session.setAttribute("loginUser", user);
        return "ok";
    }

    /**
     * 注册
     */
    public String register(String username, String password, String nickname, String email) {
        if (username == null || username.trim().isEmpty()) {
            return "用户名不能为空";
        }
        if (password == null || password.length() < 6) {
            return "密码至少6位";
        }
        if (userRepository.existsByUsername(username)) {
            return "用户名已存在";
        }
        if (email != null && !email.isEmpty() && userRepository.existsByEmail(email)) {
            return "邮箱已被注册";
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(password);
        user.setNickname((nickname != null && !nickname.trim().isEmpty()) ? nickname.trim() : username.trim());
        user.setEmail((email != null && !email.trim().isEmpty()) ? email.trim() : null);

        userRepository.save(user);
        return "ok";
    }

    /**
     * 找回密码 - 通过邮件发送密码到注册邮箱
     */
    public String findPassword(String username, String email) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "用户不存在";
        }
        if (email == null || !email.equals(user.getEmail())) {
            return "邮箱验证失败，请检查注册邮箱";
        }

        boolean sent = emailService.sendPasswordResetEmail(user);
        if (sent) {
            return "密码已发送到您的注册邮箱，请注意查收";
        }
        return "邮件发送失败，请稍后重试或联系管理员";
    }

    /** 获取当前登录用户 */
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("loginUser");
    }

    /** 更新个人资料 */
    public String updateProfile(Integer userId, String nickname, String email, String phone) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "用户不存在";

        if (nickname != null && !nickname.trim().isEmpty()) user.setNickname(nickname.trim());
        if (email != null && !email.trim().isEmpty()) user.setEmail(email.trim());
        if (phone != null && !phone.trim().isEmpty()) user.setPhone(phone.trim());

        userRepository.save(user);
        return "ok";
    }

    /** 根据ID获取用户（刷新用） */
    public User getUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    /** 修改密码 */
    public String changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "用户不存在";
        if (!user.getPassword().equals(oldPassword)) return "原密码错误";
        if (newPassword == null || newPassword.length() < 6) return "新密码至少6位";

        user.setPassword(newPassword);
        userRepository.save(user);
        return "ok";
    }
}
