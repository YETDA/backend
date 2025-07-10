package com.funding.backend.domain.order.dto.response;

import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.orderOption.entity.OrderOption;
import com.funding.backend.domain.purchaseOption.dto.response.PurchaseOptionDto;
import com.funding.backend.domain.purchaseOption.entity.PurchaseOption;
import com.funding.backend.global.toss.enums.OrderStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponseDto {
    private Long totalAmount;			// 지불금액
    private String orderId;			// 주문 고유 ID
    private String orderName;		// 주문 상품 이름
    private String customerEmail;		// 구매자 이메일
    private String customerName;		// 구매자 이름
    private String createDate;		// 결제 날짜
    private List<PurchaseOptionDto> purchaseOptions;

    public static PurchaseOrderResponseDto from(Order order, Long amount, List<PurchaseOptionDto> options) {
        return PurchaseOrderResponseDto.builder()
                .orderId(order.getOrderId())
                .orderName(order.getProject().getTitle())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .createDate(order.getCreatedAt().toString())
                .totalAmount(amount)
                .purchaseOptions(options)
                .build();
    }
}
