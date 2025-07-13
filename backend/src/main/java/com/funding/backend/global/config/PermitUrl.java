package com.funding.backend.global.config;

public class PermitUrl {

    //모든 메서드 요청 허용
    public static final String[] ALL_URLS = {

    };
    public static final String[] GET_URLS = {
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
            "/oauth2/**", "/api/v1/user/logout",
            //프로젝트 검색
            "/api/v1/project/**",
            //공지사항
            "/api/v1/notice/project/**",
            //좋아요
            "/api/v1/like/project/**/count",

            //후원
            "/api/v1/project/donation/**",

            //리뷰
            "/api/v1/reviews/project/**",

            //조회
            "api/vi/project/search"
    };

    public static final String[] POST_URLS = {
            "/api/v1/token/**"
    };

    public static final String[] PUT_URLS = {

    };

    public static final String[] DELETE_URLS = {

    };

    public static final String[] PATCH_URLS = {
            // 다른 PATCH 엔드포인트가 있으면 여기에 추가
            "/api/v1/admin/users/*/status",
    };



}