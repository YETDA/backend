package com.funding.backend.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccountInfo {
    private String bank;     // ex. 카카오뱅크
    private String account;  // ex. 3333-01-1234567
}
