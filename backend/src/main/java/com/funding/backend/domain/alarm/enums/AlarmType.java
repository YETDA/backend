package com.funding.backend.domain.alarm.enums;

public enum AlarmType {

    // 매니저 알림
    PURCHASE_PROJECT_REQUEST("구매 프로젝트 승인 요청"),


    //회원 구매 알림
    PROJECT_PURCHASED("누군가 프로젝트를 구매했습니다"),
    PROJECT_PURCHASE_SUCCESS("창작물 구매가 완료되었습니다.");


    private final String description;

    AlarmType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

