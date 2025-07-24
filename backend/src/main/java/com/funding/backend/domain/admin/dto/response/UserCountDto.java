package com.funding.backend.domain.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCountDto {
    private long totalUsers;
    private long activeUsers;
    private long stopUsers;
}
