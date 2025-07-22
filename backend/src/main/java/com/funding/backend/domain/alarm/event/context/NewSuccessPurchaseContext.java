package com.funding.backend.domain.alarm.event.context;


import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.global.toss.enums.OrderStatus;
import com.funding.backend.global.toss.enums.TossPaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewSuccessPurchaseContext {
    Long userId;
    String title;
    //구매형, 후원형
    ProjectStatus projectStatus;

    //결제 완료 상태
    TossPaymentStatus tossPaymentStatus;

    //총 결제 금액
    Long totalPaymentAmount;

    //해당 프로젝트에서 구매한 창작물 개수
    Long orderCount;
}
