package com.musiclogin.controller;

import com.musiclogin.dto.LoginRequest;
import com.musiclogin.dto.RegisterRequest;
import com.musiclogin.entity.User;
import com.musiclogin.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证控制器 - 处理首页/登录/注册/找回密码/个人中心等
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // ===================== 页面路由 =====================

    /** 主界面 - 品牌展示页 */
    @GetMapping("/")
    public String index(HttpSession session) {
        if (userService.getCurrentUser(session) != null) {
            return "redirect:/dashboard";
        }
        return "index";
    }

    /** 登录页 - 支持记住我功能 */
    @GetMapping("/login")
    public String loginPage(HttpSession session, HttpServletRequest request, Model model) {
        if (userService.getCurrentUser(session) != null) {
            return "redirect:/dashboard";
        }
        // 读取记住我的Cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("remembered_username".equals(c.getName())) {
                    model.addAttribute("savedUsername", c.getValue());
                    model.addAttribute("remembered", true);
                    break;
                }
            }
        }
        return "login";
    }

    /** 处理登录提交 - 支持记住我 */
    @PostMapping("/login")
    public String doLogin(LoginRequest req, 
                          @RequestParam(value = "rememberMe", defaultValue = "false") boolean rememberMe,
                          HttpSession session, 
                          HttpServletResponse response,
                          Model model) {
        String msg = userService.login(req.getUsername(), req.getPassword(), session);
        if ("ok".equals(msg)) {
            // 记住我 - 将用户名存入Cookie（7天有效期）
            Cookie cookie = new Cookie("remembered_username", req.getUsername());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            if (rememberMe) {
                cookie.setMaxAge(7 * 24 * 60 * 60); // 7天
            } else {
                cookie.setMaxAge(0); // 不记住就删除Cookie
            }
            response.addCookie(cookie);
            return "redirect:/dashboard";
        }
        model.addAttribute("msg", msg);
        // 保留输入的用户名
        model.addAttribute("savedUsername", req.getUsername());
        return "login";
    }

    /** 注册页 */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /** 处理注册提交 */
    @PostMapping("/register")
    public String doRegister(RegisterRequest req, Model model) {
        String msg = userService.register(req.getUsername(), req.getPassword(), req.getNickname(), req.getEmail());
        if ("ok".equals(msg)) {
            model.addAttribute("msg", "注册成功，请登录");
            return "login";
        }
        model.addAttribute("msg", msg);
        return "register";
    }

    /** 找回密码页 */
    @GetMapping("/forgot")
    public String forgotPage() {
        return "forgot";
    }

    /** 处理找回密码 - 通过邮件发送 */
    @PostMapping("/forgot")
    public String doForgot(String username, String email, Model model) {
        String msg = userService.findPassword(username, email);
        model.addAttribute("msg", msg);
        return "forgot";
    }

    /** Dashboard 主页（需登录） */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = userService.getCurrentUser(session);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "dashboard";
    }

    /** 个人中心（需登录） */
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = userService.getCurrentUser(session);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "profile";
    }

    /** 退出登录 */
    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();
        // 清除记住我的Cookie
        Cookie cookie = new Cookie("remembered_username", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/login";
    }

    // ===================== AJAX API =====================

    /** 更新个人资料（AJAX） */
    @PostMapping("/api/profile/update")
    @ResponseBody
    public Map<String, Object> updateProfile(HttpSession session,
                                             String nickname, String email, String phone) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.getCurrentUser(session);
        if (user == null) {
            result.put("success", false); result.put("msg", "未登录");
            return result;
        }
        String msg = userService.updateProfile(user.getId(), nickname, email, phone);
        result.put("success", "ok".equals(msg));
        result.put("msg", "ok".equals(msg) ? "保存成功" : msg);

        if ("ok".equals(msg)) {
            User refreshed = userService.getUserById(user.getId());
            if (refreshed != null) {
                session.setAttribute("loginUser", refreshed);
            }
        }
        return result;
    }

    /** 修改密码（AJAX） */
    @PostMapping("/api/password/change")
    @ResponseBody
    public Map<String, Object> changePassword(HttpSession session,
                                              String oldPassword, String newPassword) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.getCurrentUser(session);
        if (user == null) {
            result.put("success", false); result.put("msg", "未登录");
            return result;
        }
        String msg = userService.changePassword(user.getId(), oldPassword, newPassword);
        result.put("success", "ok".equals(msg));
        result.put("msg", "ok".equals(msg) ? "密码修改成功" : msg);
        return result;
    }
}
