package com.funding.backend.domain.alarm.service;

import com.funding.backend.domain.alarm.dto.request.AlarmRequestDto;
import com.funding.backend.domain.alarm.entity.Alarm;
import com.funding.backend.domain.alarm.repository.AlarmRepository;
import com.funding.backend.domain.alarm.repository.EmitterRepository;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.TokenService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserService userService;
    private final TokenService tokenService;
    private final EmitterRepository emitterRepository;

    @Value("${alarm.timeout}")
    private Long sseTimeout;

    @Transactional
    public SseEmitter createSseConnection() throws IOException {
        User user =userService.findUserById(tokenService.getUserIdFromAccessToken());

        SseEmitter emitter = new SseEmitter(sseTimeout);
        emitterRepository.save(user.getId(), emitter);

        emitter.onCompletion(() -> emitterRepository.delete(user.getId()));
        emitter.onTimeout(() -> emitterRepository.delete(user.getId()));
        emitter.onError((e) -> emitterRepository.delete(user.getId()));

        // 연결 확인용 더미 이벤트
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결 완료"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }
}
