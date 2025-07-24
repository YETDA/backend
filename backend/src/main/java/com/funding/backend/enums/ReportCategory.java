package com.funding.backend.enums;

public enum ReportCategory {
    TERM_VIOLATION("이용약관 또는 프로젝트 심사 기준을 위반"),
    COMMUNITY_VIOLATION("커뮤니티 운영원칙을 위반"),
    PRIVACY_VIOLATION("개인정보 보호 권리를 침해"),
    INTELLECTUAL_PROPERTY_VIOLATION("지식재산권을 침해"),
    PROJECT_IMPLEMENTATION_ISSUE("프로젝트 이행의 문제");

    private final String label;

    ReportCategory(String label) {this.label = label;}

}

