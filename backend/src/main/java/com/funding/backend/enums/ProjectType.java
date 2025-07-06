package com.funding.backend.enums;

public enum ProjectType {
    DONATION("기부형 프로젝트"),
    PURCHASE("구매형 프로젝트");

    private final String label;

    ProjectType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}