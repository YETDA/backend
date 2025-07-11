package com.funding.backend.domain.follow.dto.response;

import com.funding.backend.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowResponseDto {

    private String name;

    public static FollowResponseDto from(User user) {
        return new FollowResponseDto(user.getName());
    }
}
