package com.biz.warning.service.impl;

import com.biz.warning.dao.EmailDAO;
import com.biz.warning.service.IEmailService;
import com.biz.warning.vo.CompanyUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Slf4j
@Service
public class EmailServiceImpl implements IEmailService {

    @Value("${spring.mail.username}")
    private String emailSender;

    @Autowired
    private EmailDAO emailDAO;

    @Autowired
    private JavaMailSender sender;

    @Override
    public void send(CompanyUser user,  String subject, String content) {
        int status = 1;
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(emailSender);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(content, true);

            int i = 0;
            while (i < 3) {
                i++;
                try {
                    sender.send(message);
                    log.info("用户[{}]邮件[{}]发送成功!!", user.getUserId(), user.getEmail());
                    break;
                } catch (Exception e) {
                    status = 0;
                    log.error("用户[" + user.getUserId() + "]邮件[" + user.getEmail() + "]发送失败: " + e.getMessage(), e);
                }
                if (status == 0 && i < 3) {
                    log.info("用户[{}]邮件[{}]发送失败, 1秒后重试!!", user.getUserId(), user.getEmail());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        try {
            emailDAO.insertEmailContent(user.getApiCode(), user.getUserId(), user.getEmail(), subject, content, status);
        } catch (Exception e) {
            log.error("用户[" + user.getUserId() + "]邮件[" + user.getEmail() + "]内容保存失败: " + e.getMessage(), e);
        }
    }
}
