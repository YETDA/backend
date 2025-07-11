package com.funding.backend.enums;

public enum ProjectStatus {
    UNDER_AUDIT("심사중"),
    REJECTED("거절됨"),
    RECRUITING("모집중"),
    COMPLETED("완료");

    private final String label;

    ProjectStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
