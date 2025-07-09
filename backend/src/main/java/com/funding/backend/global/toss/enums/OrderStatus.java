package com.funding.backend.global.toss.enums;

public enum OrderStatus {
    PENDING,    // 결제 전 or 결제 시도 중
    COMPLETED,  // 결제 성공
    FAILED,     // 결제 실패
    CANCELED    // 사용자 요청에 의한 취소
}
