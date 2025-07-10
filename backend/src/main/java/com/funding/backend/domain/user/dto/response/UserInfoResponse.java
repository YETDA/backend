package com.funding.backend.domain.user.dto.response;

import com.funding.backend.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserInfoResponse {

    private Long id;
    private String name;
    private String email;
    private String image;
    private String account;
    private String bank;
    private String introduce;
    private String portfolioAddress;
    private String ssoProvider;
    private String role;
    private String userActive;

    public static UserInfoResponse from(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .image(user.getImage())
                .account(user.getAccount())
                .bank(user.getBank())
                .introduce(user.getIntroduce())
                .portfolioAddress(user.getPortfolioAddress())
                .ssoProvider(user.getSsoProvider())
                .role(user.getRole().getRole().name())
                .userActive(user.getUserActive().name())
                .build();
    }
}