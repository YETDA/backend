package com.funding.backend.domain.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.funding.backend.enums.UserActive;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserListDto {
    private Long id;
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime joinedAt;
    
    private String email;
    private long postCount;
    private int reportCount;
    private UserActive status;
}