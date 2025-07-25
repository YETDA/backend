package com.funding.backend.domain.order.dto.response;

import com.funding.backend.domain.order.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(name = "Donation Order Response DTO", description = "기부 주문 응답 DTO")
public class DonationOrderResponseDto {

    @Schema(description = "주문 ID", example = "51")
    private Long id;

    @Schema(description = "주문 고유 ID", example = "order_SRG1D45H")
    private String orderId;

    @Schema(description = "프로젝트 ID", example = "23")
    private Long projectId;

    @Schema(description = "프로젝트 제목", example = "환경 보호 프로젝트")
    private String projectTitle;

    @Schema(description = "기부 금액", example = "10000")
    private Long prince;

    @Schema(description = "후원 마감일", example = "2023-10-01T12:00:00")
    private LocalDateTime endDate;

    public DonationOrderResponseDto(Order order) {
        this.id = order.getId();
        this.orderId = order.getOrderId();
        this.projectId = order.getProject() != null ? order.getProject().getId() : null;
        this.projectTitle = order.getProject() != null ? order.getProject().getTitle() : null;
        this.prince = order.getPaidAmount();
        this.endDate = order.getProject() != null ? order.getProject().getDonation().getEndDate() : null;
    }
}
