package com.funding.backend.domain.settlement.service;

import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.order.service.OrderService;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.settlement.dto.response.SettlementDetailResponseDto;
import com.funding.backend.domain.settlement.entity.Settlement;
import com.funding.backend.domain.settlement.repository.SettlementRepository;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.enums.SettlementStatus;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.toss.enums.TossPaymentStatus;
import com.funding.backend.security.jwt.TokenService;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {

    @Value("${settlement.day}")
    private Long settlementDay;

    private final SettlementRepository settlementRepository;
    private final OrderService orderService;

    private final ProjectService projectService;
    private final UserService userService;
    private final TokenService tokenService;


    //프로젝트 아이디를 입력하면, 해당 프로젝트에 대한 정산 내역을 제공
    public SettlementDetailResponseDto getLatestPurchaseSettlementDetail(Long projectId){
        Project project = projectService.findProjectById(projectId);
        User loginUser = userService.findUserById(tokenService.getUserIdFromAccessToken());

        //해당 메서드 사용하기 위한 유효성 검사 로직
        validPurchaseSettlement(project,loginUser);
        List<Settlement> settlementList = settlementRepository.findAllByProject(project);

        if (settlementList == null || settlementList.isEmpty()) {
            //정산 내역이 존재하지 않는다면
            return getFromProjectCreation(project);
        } else {

            //정산 내역이 존재한다면
            return getFromLatestSettlement(project, settlementList);
        }
    }


    //정산 내역이 존재하지 않는다면 -> 프로젝트 생성일 기준, 현재 날짜 기준으로 제공
    private SettlementDetailResponseDto getFromProjectCreation(Project project) {
        LocalDateTime from = project.getCreatedAt();
        LocalDateTime to = LocalDateTime.now();

        List<Order> orders = orderService.findByProjectAndCreatedAtBetween(project, from, to, TossPaymentStatus.DONE);
        return calculateSettlementDto(project, from, to, orders); // isPreview = true
    }

    //정산 내역이 존재한다면
    private SettlementDetailResponseDto getFromLatestSettlement(Project project, List<Settlement> settlements) {
        //이전 정산내역의 마지막 정산 날짜를 계산하고
        Settlement latest = settlementRepository.findTopByProjectOrderByPeriodEndDesc(project)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.SETTLEMENT_NOT_FOUND));

        // 나노초 뒤 부터 주문 내역 계산
        LocalDateTime from = latest.getPeriodEnd().plusNanos(1);
        LocalDateTime to = LocalDateTime.now();

        List<Order> orders = orderService.findByProjectAndCreatedAtBetween(project, from, to,TossPaymentStatus.DONE);
        return calculateSettlementDto(project, from, to, orders);
    }




    public void validPurchaseSettlement(Project project, User loginUser){
        //해당 프로젝트의 주인이 아니라면 정산 처리 못하는 예외 로직 추가
        projectService.validProjectUser(project.getUser(),loginUser);

        //구매형 전용 예외 처리
        if (project.getProjectType() != ProjectType.PURCHASE) {
            throw new BusinessLogicException(ExceptionCode.INVALID_PROJECT_TYPE);
        }
    }


    private SettlementDetailResponseDto calculateSettlementDto(
            Project project,
            LocalDateTime from,
            LocalDateTime to,
            List<Order> orders
    ) {
        long totalAmount = orders.stream().mapToLong(Order::getPaidAmount).sum();
        long fee = (long) (totalAmount * 0.1);
        long payout = totalAmount - fee;

        return SettlementDetailResponseDto.builder()
                .projectTitle(project.getTitle())
                .periodStart(from)
                .periodEnd(to)
                .totalOrderAmount(totalAmount)
                .feeAmount(fee)
                .payoutAmount(payout)
                .settlementStatus(SettlementStatus.WAITING)
                .build();
    }



}
