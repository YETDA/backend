package com.funding.backend.domain.alarm.enums;

public enum AlarmType {

    // 매니저 알림
    PROJECT_REQUEST("프로젝트 승인 요청");

    private final String description;

    AlarmType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

