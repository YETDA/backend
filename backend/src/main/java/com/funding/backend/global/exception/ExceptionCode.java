package com.funding.backend.global.exception;

import lombok.Getter;

public enum ExceptionCode {
    //프로젝트 예외 처리
    PROJECT_NOT_FOUND(404,"존재하지 않는 프로젝트 입니다.")

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