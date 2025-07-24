package com.funding.backend.domain.alarm.dto.response;

import com.funding.backend.domain.alarm.entity.Alarm;
import com.funding.backend.domain.alarm.enums.AlarmType;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.SettlementStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AlarmListResponseDto {
    private String content;
    private AlarmType alarmType;
    private LocalDateTime createdAt;
    private Long userId;
    private Long alarmId;
    private boolean readStatus;


    public static AlarmListResponseDto from(Alarm alarm) {
        return AlarmListResponseDto.builder()
                .alarmId(alarm.getId())
                .content(alarm.getMessage())
                .alarmType(alarm.getAlarmType())
                .readStatus(alarm.isReadStatus())
                .createdAt(alarm.getCreatedAt())
                .userId(alarm.getId())
                .build();
    }
}
