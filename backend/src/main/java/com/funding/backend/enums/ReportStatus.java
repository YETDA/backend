package com.funding.backend.enums;

public enum ReportStatus {
    PENDING("대기"),
    REVIEWING("검토"),
    APPROVED("승인"),
    REJECTED("거부");

    private final String label;

    ReportStatus(String label) {this.label = label;}
}
