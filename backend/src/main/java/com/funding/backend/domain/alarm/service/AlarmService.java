package com.funding.backend.domain.alarm.service;

import com.funding.backend.domain.alarm.dto.response.AlarmDto;
import com.funding.backend.domain.alarm.dto.response.AlarmResponseDto;
import com.funding.backend.domain.alarm.dto.response.AlarmListResponseDto;
import com.funding.backend.domain.alarm.entity.Alarm;
import com.funding.backend.domain.alarm.repository.AlarmRepository;
import com.funding.backend.domain.alarm.repository.EmitterRepository;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.TokenService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Service
@Slf4j
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

    @Transactional
    public Alarm createNotification(AlarmDto request) {
        User user = userService.findUserById(request.getUserId());
        String finalMessage = request.getMessage();

        Alarm notification = Alarm.builder()
                .alarmType(request.getAlarmType())
                .message(finalMessage)
                .readStatus(false)
                .user(user)
                .build();

        Alarm saved = alarmRepository.save(notification);

        // SSE로 전송할 때 DTO 사용
        SseEmitter emitter = emitterRepository.get(user.getId());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("alarm")  // 이벤트 이름 지정
                        .data(AlarmResponseDto.from(saved)));
                log.info("1111✅ 알림 저장 시도: userId={}, msg={}", user.getId(), finalMessage);
            } catch (IOException e) {
                emitterRepository.delete(user.getId());
                emitter.completeWithError(e);
            }
        }
        log.info("2222✅ 알림 저장 시도: userId={}, msg={}", user.getId(), finalMessage);

        return saved;
    }

    public Page<AlarmListResponseDto> getUserAlarmList(Boolean readStatus, Pageable pageable){
        User user = userService.findUserById(tokenService.getUserIdFromAccessToken());

        if (readStatus == null) {
            return alarmRepository.findAllByUser(user, pageable)
                    .map(AlarmListResponseDto::from);
        } else {
            return alarmRepository.findByUserAndReadStatus(user, readStatus, pageable)
                    .map(AlarmListResponseDto::from);
        }
    }


    @Transactional
    public void readAlarm(Long alarmId) {
        Alarm alarm = findAlarmById(alarmId);
        //유저 식별 검사
        validUser(alarm);

        //알람이 읽음 처리가 아닌 경우에만 변경
        if (!alarm.isReadStatus()) {
           alarm.setReadStatus(true);
        }
    }


    @Transactional
    public void readAllUserAlarms(){
        User user = userService.findUserById(tokenService.getUserIdFromAccessToken());
        List<Alarm> alarmList = alarmRepository.findByUserAndReadStatus(user, false);

        for (Alarm alarm : alarmList) {
            alarm.setReadStatus(true);
        }

    }

    public void deleteAlarm(Long alarmId){
        Alarm alarm = findAlarmById(alarmId);
        validUser(alarm);
        alarmRepository.delete(alarm);
    }



    public void validUser(Alarm alarm){
        User user = userService.findUserById(tokenService.getUserIdFromAccessToken());
        if(!alarm.getUser().equals(user)){
            throw new BusinessLogicException(ExceptionCode.ALARM_FORBIDDEN);
        }
    }

    public Alarm findAlarmById(Long alarmId){
        return alarmRepository.findById(alarmId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.ALARM_NOT_FOUND));
    }









}
