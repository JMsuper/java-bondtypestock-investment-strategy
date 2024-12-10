package com.finance.adam.util;

import com.finance.adam.repository.notification.domain.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j // SLF4J 사용
public class MyMailSender {

    private final JavaMailSender javaMailSender;

    public void sendNotificationEmail(Notification notification) {
        // 수신자 이메일 주소를 Notification의 Account에서 가져옴
        String recipientEmail = notification.getAccount().getEmail();
        String subject = notification.getSubject();
        String content = notification.getContent();

        log.info("Preparing to send email to: {}", recipientEmail);

        try {
            sendEmail(recipientEmail, subject, content);
            log.info("Email sent successfully to: {}", recipientEmail);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}. Error: {}", recipientEmail, e.getMessage(), e);
        }
    }

    private void sendEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        // 송신자 정보 설정 (이메일 주소와 송신자명)
        try {
            helper.setFrom("wjdalsokgo@gmail.com", "snowball-stock");
        } catch (UnsupportedEncodingException e) {
            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage(), e);
        }

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true); // HTML 형식의 이메일 전송을 위해 true 설정

        log.debug("Email details - To: {}, Subject: {}, Content: {}", to, subject, text);

        javaMailSender.send(message);
    }
}
