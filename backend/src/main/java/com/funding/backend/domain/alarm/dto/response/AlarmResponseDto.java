package com.funding.backend.domain.alarm.dto.response;

import com.funding.backend.domain.alarm.entity.Alarm;
import com.funding.backend.domain.alarm.enums.AlarmType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlarmResponseDto {
    private Long id;
    private String message;
    private AlarmType alarmType;
    private boolean readStatus;
    private LocalDateTime createdAt;

    public static AlarmResponseDto from(Alarm alarm) {
        return AlarmResponseDto.builder()
                .id(alarm.getId())
                .message(alarm.getMessage())
                .alarmType(alarm.getAlarmType())
                .readStatus(alarm.isReadStatus())
                .createdAt(alarm.getCreatedAt())
                .build();
    }
}