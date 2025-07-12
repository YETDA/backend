package com.funding.backend.global.exception;

import lombok.Getter;

public enum ExceptionCode {

    // 토큰 누락
    ACCESS_TOKEN_NOT_FOUND(401, "Access Token이 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(401, "Refresh Token이 존재하지 않습니다."),
    // 토큰 유효성 실패
    INVALID_ACCESS_TOKEN(401, "Access Token이 유효하지 않습니다."),
    INVALID_REFRESH_TOKEN(401, "Refresh Token이 유효하지 않습니다."),

    // 사용자정보 예외 처리
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    NAME_CANNOT_BE_EMPTY(400, "이름은 공란일 수 없습니다."),
    EMAIL_ALREADY_EXISTS(409, "이미 사용 중인 이메일입니다."),
    EMAIL_NOT_VERIFIED(401, "이메일 인증이 필요합니다."),
    EMAIL_SEND_FAILED(500, "이메일 인증 코드 전송에 실패했습니다."),
    EMAIL_VERIFICATION_NOT_FOUND(404, "인증 요청이 존재하지 않습니다."),
    EMAIL_VERIFICATION_FAILED(401, "인증 코드가 일치하지 않습니다."),
    BANK_AND_ACCOUNT_REQUIRED(400, "은행명과 계좌번호를 모두 입력해야 합니다."),
    ROLE_NOT_FOUND(404, "존재하지 않는 역할입니다."),
    ADMIN_ROLE_REQUIRED(403, "관리자 권한이 필요합니다."),

    //유저 예외 처리
    BANK_NOT_FOUND(404, "은행이 존재하지 않습니다."),
    ACCOUNT_NOT_FOUND(404, "계좌가 존재하지 않습니다."),

    //팔로우 예외 처리
    FOLLOW_NOT_FOUND(404, "팔로우 관계를 찾을 수 없습니다"),
    ALREADY_FOLLOWING(400, "이미 팔로우 중입니다"),
    CANNOT_FOLLOW_SELF(400, "자기 자신을 팔로우할 수 없습니다"),

    //프로젝트 예외 처리
    PROJECT_NOT_FOUND(404, "존재하지 않는 프로젝트 입니다."),
    NOT_PROJECT_CREATOR(403, "해당 프로젝트의 생성자가 아닙니다."),
    INVALID_PROJECT_TYPE(400, "지원하지 않는 프로젝트 타입입니다."),
    INVALID_PROJECT_SEARCH_TYPE(400, "지원하지 않는 프로젝트 검색 타입입니다."),
    PROJECT_CANNOT_BE_REVIEWED(400, "프로젝트가 심사될 수 없는 상태입니다."),

    //검색 예외 처리
    INVALID_SEARCH_KEYWORD(400, "2글자 이상 입력하세요."),

    //공지사항 예외 처리
    NOTICE_NOT_FOUND(404, "존재하지 않는 공지사항 입니다."),

    //주문 예외처리
    ORDER_NOT_FOUND(404, "존재하지 않는 구매 내역 입니다. "),

    //구매 예외처리
    PURCHASE_NOT_FOUND(404, "존재하지 않는 구매 프로젝트 입니다. "),
    INVALID_PROVIDING_METHOD(400, "제공 방식이 유효하지 않습니다."),

    //사용자 예외처리

    //구매 카테고리 예외처리
    PURCHASE_CATEGORY_NOT_FOUND(404, "존재하지 않는 구매 프로젝트 카테고리 입니다. "),

    //구매 옵션 예외처리
    PURCHASE_OPTION_NOT_FOUND(404, "존재하지 않는 구매옵션 입니다."),
    PURCHASE_OPTION_FILE_COUNT(404, "옵션 개수와 파일 개수가 일치하지 않습니다."),
    UNSUPPORTED_PROVIDING_METHOD(400, "지원하지 않는 제공 방식입니다."),
    FILE_REQUIRED_FOR_DOWNLOAD_OPTION(400, "DOWNLOAD 방식의 구매 옵션에는 파일이 필수입니다."),
    PURCHASE_OPTION_FILE_NOT_FOUND(400, "해당 옵션에 매칭되는 파일을 찾을 수 없습니다."),
    UNSUPPORTED_PROJECT_TYPE_ORDER(400,"구매 API는 구매형 프로젝트에만 적용됩니다"),

    //S3 예외 처리
    S3_DELETE_ERROR(404, "이미지를 삭제할 수 없습니다."),
    IMAGE_NOT_FOUND(404, "이미지를 찾을 수 없습니다."),
    IMAGE_UPLOAD_FAILED(500, "이미지 업로드에 실패하였습니다."),
    FILE_UPLOAD_FAILED(500, "파일 업로드에 실패하였습니다."),
    INVALID_S3_URL_FORMAT(400, "잘못된 S3 URL 형식입니다."),
    ETAG_HASH_FAILED(500, "ETag 해시 계산에 실패하였습니다."),
    MD5_HASH_FAILED(500, "MD5 해시 생성에 실패하였습니다."),

    //QnA 예외 처리
    QNA_NOT_FOUND(404, "존재하지 않는 QnA입니다."),
    QNA_ACCESS_DENIED(403, "접근 권한이 없습니다."),
    ANSWER_NOT_FOUND(404, "답변이 존재하지 않습니다."),
    ANSWER_ALREADY_EXISTS(409, "이미 답변이 존재합니다."),

    //요금제 예외 처리
    PRICING_PLAN_NOT_FOUND(404, "존재하지 않는 요금제 입니다");

    @Getter
    private final int status;

    @Getter
    private final String message;

    ExceptionCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}