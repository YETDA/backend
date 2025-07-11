package com.funding.backend.domain.user.email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerification {  // redis 임시 저장 용도
    private String email;
    private String code;
    private boolean verified;
}