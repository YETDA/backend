package com.funding.backend.domain.user.email.service;

import com.funding.backend.domain.user.email.dto.EmailVerification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long EXPIRE_MINUTES = 5;

    // 인증코드 메일 전송
    public void sendVerificationCode(String email) {
        String code = String.valueOf((int) ((Math.random() * 900000) + 100000)); // 6자리 숫자
        EmailVerification verification = new EmailVerification(email, code, false);
        redisTemplate.opsForValue().set("email:" + email, verification, Duration.ofMinutes(EXPIRE_MINUTES));

        // 메일 전송
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(email);
            helper.setFrom("noreply@yetda.com", "YETDA 공식 메일"); // 이메일 + 이름
            helper.setSubject("[YETDA] 이메일 인증 코드입니다.");

            String html = "<div style='font-family: Arial, sans-serif; font-size: 16px;'>"
                    + "<p>안녕하세요, YETDA입니다 😊</p>"
                    + "<p>아래 <strong>인증 코드</strong>를 입력해주세요:</p>"
                    + "<div style='margin-top: 10px; padding: 10px; background: #f5f5f5; border-radius: 5px; font-size: 24px; text-align: center;'>"
                    + code
                    + "</div>"
                    + "<p style='margin-top: 20px;'>이 코드는 <b>5분간</b> 유효합니다.</p>"
                    + "</div>";

            helper.setText(html, true); // HTML 형식

            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    // 인증코드 검증
    public boolean verifyCode(String email, String inputCode) {
        EmailVerification stored = (EmailVerification) redisTemplate.opsForValue().get("email:" + email);
        if (stored != null && stored.getCode().equals(inputCode)) {
            stored.setVerified(true);
            redisTemplate.opsForValue().set("email:" + email, stored, Duration.ofMinutes(3)); // 인증 완료 상태로 재저장
            return true;
        }
        return false;
    }

    // 인증 여부 조회
    public boolean isVerified(String email) {
        EmailVerification stored = (EmailVerification) redisTemplate.opsForValue().get("email:" + email);
        return stored != null && stored.isVerified();
    }
}
