package com.funding.backend.domain.project.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 🔹 구매형 프로젝트 DTO
@Getter
@Setter
@NoArgsConstructor
public class PurchaseProjectDetail {
    @Pattern(regexp = "^(DOWNLOAD|EMAIL)$", message = "전송 방식은 DOWNLOAD 또는 EMAIL이어야 합니다.")
    private String deliveryMethod;// DOWNLOAD, EMAIL 등
    private Integer downloadLimit;
    private String fileUrl;
}
