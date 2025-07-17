package com.funding.backend.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "Donation Order Request DTO", description = "기부 주문 요청 DTO")
public class DonationOrderRequestDto {

    @Schema(description = "프로젝트 ID", example = "1")
    private Long projectId;

    @Schema(description = "고객 이메일", example = "example@email.com")
    private String email;

    @Schema(description = "기부 금액", example = "10000")
    private Long price;
}
