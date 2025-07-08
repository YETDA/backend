package com.funding.backend.global.exception;

import lombok.Getter;

public enum ExceptionCode {
    //프로젝트 예외 처리
    PROJECT_NOT_FOUND(404,"존재하지 않는 프로젝트 입니다."),
    NOT_PROJECT_CREATOR(403, "해당 프로젝트의 생성자가 아닙니다."),
    INVALID_PROJECT_TYPE(400, "지원하지 않는 프로젝트 타입입니다."),


    //공지사항 예외 처리
    NOTICE_NOT_FOUND(404, "존재하지 않는 공지사항 입니다."),

    //구매 예외처리
    PURCHASE_NOT_FOUND(404, "존재하지 않는 구매 프로젝트 입니다. "),

    //구매 카테고리 예외처리
    PURCHASE_CATEGORY_NOT_FOUND(404, "존재하지 않는 구매 프로젝트 카테고리 입니다. "),


    //구매 옵션 예외처리
    PURCHASE_OPTION_FILE_COUNT(404,"옵션 개수와 파일 개수가 일치하지 않습니다."),
    //S3 예외 처리
    S3_DELETE_ERROR(404, "이미지를 삭제할 수 없습니다."),
    IMAGE_NOT_FOUND(404,"이미지를 찾을 수 없습니다."),



    //요금제 예외 처리
    PRICING_PLAN_NOT_FOUND(404,"존재하지 않는 요금제 입니다");

    ;

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}