package com.funding.backend.global.toss.enums;

public enum PayType {
    VIRTUAL_ACCOUNT("가상계좌"),
    EASY_PAY("간편결제"),
    GAME_GIFT_CARD("게임문화상품권"),
    ACCOUNT_TRANSFER("계좌이체"),
    BOOK_GIFT_CARD("도서문화상품권"),
    CULTURE_GIFT_CARD("문화상품권"),
    CARD("카드"),
    MOBILE("휴대폰");

    private final String label;

    PayType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}