package com.funding.backend.domain.order.dto.response;

import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.orderOption.dto.response.OrderOptionResponseDto;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.toss.enums.PayType;
import com.funding.backend.global.toss.enums.TossPaymentStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private String orderId;
    private TossPaymentStatus orderStatus;
    private PayType payType;
    private Long paidAmount;
    private ProjectType projectType;
    private String customerName;
    private String customerEmail;

    private String projectTitle; // 프로젝트 명 (선택)

    //구매형일때만 정보 return
    private List<OrderOptionResponseDto> orderOptions;

    public static OrderResponseDto from(Order order) {
        List<OrderOptionResponseDto> optionDtos = null;

        if (order.getProjectType() != ProjectType.DONATION) {
            optionDtos = order.getOrderOptionList().stream()
                    .map(OrderOptionResponseDto::from)
                    .toList();
        }

        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus())
                .payType(order.getPayType())
                .paidAmount(order.getPaidAmount())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .projectType(order.getProjectType())
                .projectTitle(order.getProject() != null ? order.getProject().getTitle() : null)
                .orderOptions(optionDtos)
                .build();
    }


}
