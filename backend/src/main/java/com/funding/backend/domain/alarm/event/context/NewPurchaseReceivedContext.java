package com.funding.backend.domain.alarm.event.context;


import com.funding.backend.domain.pricingPlan.entity.PricingPlan;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.toss.enums.TossPaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewPurchaseReceivedContext {
    //구매한 유저의 아이디
    Long userId;

    //구매한 유저의 닉네임
    String userName;

    //프로젝트 주인 유저 아이디
    Long projectUserId;

    //결제 완료 상태
    TossPaymentStatus tossPaymentStatus;

    String title;

    //총 결제 금액
    Long totalPaymentAmount;

    //해당 프로젝트에서 구매한 창작물 개수
    Long orderCount;

}
