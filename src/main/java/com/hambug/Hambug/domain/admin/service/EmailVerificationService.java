package com.hambug.Hambug.domain.admin.service;

import com.hambug.Hambug.domain.admin.entity.AdminUser;
import com.hambug.Hambug.domain.admin.entity.EmailVerification;
import com.hambug.Hambug.domain.admin.repository.EmailVerificationRepository;
import com.hambug.Hambug.global.email.EmailMessage;
import com.hambug.Hambug.global.email.EmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;

    public EmailVerification findById(Long id) {
        return emailVerificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("이메일 인증코드를 찾을수 없습니다."));
    }

    public EmailVerification create(AdminUser adminUser) {
        EmailVerification ev = adminUser.getEmailVerification();
        String code;
        if (ev == null) {
            ev = EmailVerification.of(adminUser);
            adminUser.setEmailVerification(ev);
            code = ev.getCode();
            emailVerificationRepository.save(ev);
        } else {
            code = ev.resent();
            ev.setVerified(false);
            emailVerificationRepository.save(ev);
        }

        try {
            EmailMessage message = EmailMessage.builder()
                    .to(adminUser.getEmail())
                    .subject("[Hambug] 관리자 이메일 인증 코드")
                    .body("안녕하세요, " + adminUser.getName() + "님!<br><br>" +
                            "아래 이메일 인증 코드를 입력해 주세요:<br>" +
                            "<h2 style='letter-spacing:3px;'>" + code + "</h2>" +
                            "코드는 5분간 유효합니다.")
                    .html(true)
                    .build();
            emailService.sendEmail(message);
        } catch (Exception e) {
            log.warn("이메일 전송 실패: {}", e.getMessage());
        }
        return ev;
    }

}
