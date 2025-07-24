package com.funding.backend.domain.alarm.dto.response;

import com.funding.backend.domain.alarm.enums.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlarmDto {
    private AlarmType alarmType;
    private String message;
    private Long userId; // 알림을 받을 유저의 ID
}
