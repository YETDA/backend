package com.funding.backend.global.config;

public class PermitUrl {

    //모든 메서드 요청 허용
    public static final String[] ALL_URLS = {

    };

    public static final String[] GET_URLS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/oauth2/**",
            "/api/v1/user/logout",

            // 프로젝트 관련
            "/api/v1/project/**",
            "/api/v1/project/search",

            // 구매 옵션
            "/api/v1/purchaseOption/**",

            // 공지사항
            "/api/v1/notice/**",

            // 좋아요
            "/api/v1/like/project/*/count",

            // 후원형
            "/api/v1/project/donation/**",

            // 리뷰
            "/api/v1/reviews/**",

            // Qna
            "/api/v1/qna/**",

            //구매 옵션
            "/api/v1/purchaseOption/**"
    };

    public static final String[] POST_URLS = {
            "/api/v1/token/**",
            "/api/v1/project/purchase/**"
    };

    public static final String[] PUT_URLS = {
            //구매형 프로젝트
            "/api/v1/project/purchase/**"
    };

    public static final String[] DELETE_URLS = {
            //구매형 프로젝트
            "/api/v1/project/purchase/**"
    };

    public static final String[] PATCH_URLS = {
            // 다른 PATCH 엔드포인트가 있으면 여기에 추가
            "/api/v1/admin/users/*/status",
    };

    public static final String[] OPTIONS_URLS = {
            "/**"
    };
}