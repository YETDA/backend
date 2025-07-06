package com.funding.backend.enums;

public enum OptionStatus {
    AVAILABLE("판매중"),
    SOLD_OUT("품절"),
    SCHEDULED("오픈예정"),
    DISCONTINUED("판매중지");

    private final String label;

    OptionStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    }