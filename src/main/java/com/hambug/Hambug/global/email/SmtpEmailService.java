package com.hambug.Hambug.global.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "email", name = "provider", havingValue = "smtp")
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.from:${spring.mail.username:}}")
    private String from;

    @Override
    @Async("mailExecutor")
    public void sendEmail(EmailMessage message) {
        try {
            log.info("에에에ㅔㅇ?");
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(message.getTo());
            if (message.getCc() != null && !message.getCc().isEmpty()) {
                helper.setCc(message.getCc().toArray(new String[0]));
            }
            if (message.getBcc() != null && !message.getBcc().isEmpty()) {
                helper.setBcc(message.getBcc().toArray(new String[0]));
            }
            helper.setSubject(message.getSubject());
            helper.setText(message.getBody(), message.isHtml());
            log.info("여기까지는 오는거지?");
            mailSender.send(mime);
        } catch (MessagingException e) {
            log.error("[EMAIL][SMTP] MessagingException while sending email: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to send email", e);
        } catch (Exception e) {
            log.error("[EMAIL][SMTP] Unexpected error while sending email: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to send email", e);
        }
    }
}
