package com.funding.backend.domain.order.dto.response;

import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.orderOption.entity.OrderOption;
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
    private String payType;			// 지불방법
    private Long amount;			// 지불금액
    private String orderId;			// 주문 고유 ID
    private String orderName;		// 주문 상품 이름
    private String customerEmail;		// 구매자 이메일
    private String customerName;		// 구매자 이름
    private String createDate;		// 결제 날짜
    private String paySuccessYn;		// 결제 성공 여부

    public PurchaseOrderResponseDto(Order order) {
        this.payType = order.getPayType().getLabel(); // enum에서 한글 라벨
        this.amount = order.getPaidAmount();
        this.orderId = order.getOrderId();
        this.orderName = order.getProject().getTitle(); // 프로젝트명 = 상품명
        this.customerEmail = order.getCustomerEmail();
        this.customerName = order.getCustomerName();
        this.createDate = order.getCreatedAt() != null ? order.getCreatedAt().toString() : null;
        this.paySuccessYn = order.getOrderStatus() == com.funding.backend.global.toss.enums.OrderStatus.COMPLETED ? "Y" : "N";
    }
}
