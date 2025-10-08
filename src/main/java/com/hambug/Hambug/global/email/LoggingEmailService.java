package com.hambug.Hambug.global.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "email", name = "provider", havingValue = "log", matchIfMissing = true)
public class LoggingEmailService implements EmailService {
    @Override
    public void sendEmail(EmailMessage message) {
        if (message == null) {
            log.warn("[EMAIL][LOG] message is null");
            return;
        }
        String body = message.getBody();
        String preview = body == null ? "" : (body.length() > 200 ? body.substring(0, 200) + "..." : body);
        log.info("[EMAIL][LOG] to={}, subject={}, html={}, cc={}, bcc={}, bodyPreview={}",
                message.getTo(), message.getSubject(), message.isHtml(), message.getCc(), message.getBcc(), preview);
    }
}
