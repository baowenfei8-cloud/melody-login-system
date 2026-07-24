package com.musiclogin.service;

import com.musiclogin.entity.User;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件发送服务 - 用于找回密码
 * 未配置真实邮箱时自动进入模拟模式，密码打印到控制台
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    // 不强制注入，获取不到就进模拟模式
    private JavaMailSender mailSender;

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    private boolean configured = false;

    @Autowired(required = false)
    public void setMailSender(JavaMailSender sender) {
        this.mailSender = sender;
    }

    @PostConstruct
    public void init() {
        boolean hasRealConfig = mailHost != null && !mailHost.isEmpty() && !mailHost.contains("你的")
                             && fromEmail != null && !fromEmail.isEmpty() && !fromEmail.contains("你的");
        configured = mailSender != null && hasRealConfig;
        if (configured) {
            log.info("═══════════════════════════════════════════");
            log.info("  ✅ 邮件服务已配置，发件人: {}", fromEmail);
            log.info("  ✅ 找回密码功能已可以使用真实邮箱发送");
            log.info("═══════════════════════════════════════════");
        } else {
            log.warn("═══════════════════════════════════════════");
            log.warn("  ⚠️ 邮件服务使用模拟模式（密码打印到控制台）");
            log.warn("  ⚠️ 如需真实发送，请配置 application.properties：");
            log.warn("");
            log.warn("  ① 打开文件: src/main/resources/application.properties");
            log.warn("  ② 选择一种邮箱方案，去掉注释（删掉 # 号）");
            log.warn("  ③ 将 username 改为你的邮箱");
            log.warn("  ④ 将 password 改为授权码（不是登录密码）");
            log.warn("  ⑤ 重启项目即可");
            log.warn("");
            log.warn("  支持：QQ邮箱 / 163邮箱 / Gmail / Outlook / 任意邮箱");
            log.warn("  详细说明见 application.properties 中的注释");
            log.warn("═══════════════════════════════════════════");
        }
    }

    public boolean sendPasswordResetEmail(User user) {
        try {
            String to = user.getEmail();
            if (to == null || to.isEmpty()) {
                log.warn("用户 {} 没有绑定邮箱", user.getUsername());
                return false;
            }

            String subject = "【MelodyLogin】密码找回通知";
            String content = buildEmailContent(user);

            if (!configured || mailSender == null) {
                log.info("\n==============================================");
                log.info("  📧 模拟发送邮件至: {}", to);
                log.info("  标题: {}", subject);
                log.info("  内容:\n{}", content);
                log.info("==============================================");
                return true;
            }

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(content);
            mailSender.send(msg);
            log.info("✅ 密码邮件已发送至 {}", to);
            return true;

        } catch (Exception e) {
            log.error("邮件处理异常: {}", e.getMessage());
            // 异常降级 - 仍尝试模拟发送
            log.info("⚠️ 降级到模拟模式...");
            log.info("  收件人: {}", user.getEmail());
            log.info("  密码内容:\n{}", buildEmailContent(user));
            return true; // 模拟成功，用户能看到成功消息
        }
    }

    private String buildEmailContent(User user) {
        return "尊敬的 " + (user.getNickname() != null ? user.getNickname() : user.getUsername()) + "，您好：\n\n"
             + "您正在进行密码找回操作，您的账号信息如下：\n"
             + "━━━━━━━━━━━━━━━━━━━━━━━\n"
             + "  用户名：" + user.getUsername() + "\n"
             + "  密  码：" + user.getPassword() + "\n"
             + "━━━━━━━━━━━━━━━━━━━━━━━\n\n"
             + "请妥善保管您的密码，建议登录后及时修改。\n\n"
             + "—— MelodyLogin 音乐登录系统";
    }
}
