package com.funding.backend.domain.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.funding.backend.enums.UserActive;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDto {
    private Long userId;
    private String name;
    private String email;
    private String ssoProvider;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime joinedAt;

    private int reportCount;
    private UserActive status;
    private String image;
}
