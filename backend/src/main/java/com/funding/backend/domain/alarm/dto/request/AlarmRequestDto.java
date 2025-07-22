package com.funding.backend.domain.alarm.dto.request;

import com.funding.backend.domain.alarm.entity.Alarm;
import com.funding.backend.domain.alarm.enums.AlarmType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlarmRequestDto {
    private Long id;
    private String message;
    private AlarmType alarmType;
    private boolean readStatus;
    private LocalDateTime createdAt;

    public static AlarmRequestDto from(Alarm alarm) {
        return AlarmRequestDto.builder()
                .id(alarm.getId())
                .message(alarm.getMessage())
                .alarmType(alarm.getAlarmType())
                .readStatus(alarm.isReadStatus())
                .createdAt(alarm.getCreatedAt())
                .build();
    }
}