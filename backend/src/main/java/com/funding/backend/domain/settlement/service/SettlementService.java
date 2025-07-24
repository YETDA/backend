package com.funding.backend.domain.settlement.service;

import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.order.service.OrderService;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.settlement.dto.response.SettlementDetailListResponseDto;
import com.funding.backend.domain.settlement.dto.response.SettlementDetailResponseDto;
import com.funding.backend.domain.settlement.dto.response.SettlementMonthlyTotalResponseDto;
import com.funding.backend.domain.settlement.entity.Settlement;
import com.funding.backend.domain.settlement.mapper.SettlementDetailListResponseMapper;
import com.funding.backend.domain.settlement.mapper.SettlementDetailResponseMapper;
import com.funding.backend.domain.settlement.mapper.SettlementDtoMapper;
import com.funding.backend.domain.settlement.repository.SettlementRepository;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.enums.SettlementStatus;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.toss.enums.TossPaymentStatus;
import com.funding.backend.security.jwt.TokenService;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
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

    private final SettlementDetailResponseMapper detailMapper;
    private final SettlementDetailListResponseMapper listMapper;

    //프로젝트 아이디를 입력하면, 해당 프로젝트에 대한 정산 내역을 제공
    public SettlementDetailResponseDto getLatestPurchaseSettlementDetail(Long projectId) {
        Project project = projectService.findProjectById(projectId);
        User user = userService.findUserById(tokenService.getUserIdFromAccessToken());
        validPurchaseSettlement(project, user);
        List<Settlement> settlements = settlementRepository.findAllByProject(project);

        return getSettlementPeriodAndCalculate(project, settlements, detailMapper);
    }

    //사용자의 구매형 프로젝트 정산 리스트 조회
    public Page<SettlementDetailListResponseDto> getPurchaseSettlementDetailsListByUser(Pageable pageable) {
        User user = userService.findUserById(tokenService.getUserIdFromAccessToken());

        Page<Project> projects = projectService.findByUserIdAndProjectTypeAndProjectStatusIn(
                user.getId(), pageable, ProjectType.PURCHASE,
                List.of(ProjectStatus.RECRUITING, ProjectStatus.COMPLETED)
        );

        return projects.map(project -> {
            validPurchaseSettlement(project, user);
            List<Settlement> settlements = settlementRepository.findAllByProject(project);
            return getSettlementPeriodAndCalculate(project, settlements, listMapper);
        });
    }


    public void validPurchaseSettlement(Project project, User loginUser) {
        //해당 프로젝트의 주인이 아니라면 정산 처리 못하는 예외 로직 추가
        projectService.validProjectUser(project.getUser(), loginUser);

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
        Long pricingPlan = project.getPricingPlan().getPaymentFee();

        long totalAmount = orders.stream().mapToLong(Order::getPaidAmount).sum();
        double feeRate = pricingPlan / 100.0;
        long fee = Math.round(totalAmount * feeRate);
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

    // 특정 사용자의, 특정 타입(기부형/구매형) 프로젝트에 대한 정산 “요청” 횟수(= Settlement 레코드 수) 조회
    public long countSettlementRequestsByType(Long userId, ProjectType projectType) {
        return settlementRepository
                .countByUserIdAndProject_ProjectType(userId, projectType);
    }

    // 특정 사용자의, 특정 타입 프로젝트에 대한 완료된(SETTLEMENT_STATUS = COMPLETED) 정산 “수익” 총액 조회
    public long sumCompletedPayoutByType(Long userId, ProjectType projectType) {
        Long sum = settlementRepository
                .sumPayoutByUserIdAndProjectType(userId, projectType);
        return sum != null ? sum : 0L;
    }

    private <T> T getSettlementPeriodAndCalculate(Project project, List<Settlement> settlements,
                                                  SettlementDtoMapper<T> mapper) {
        LocalDateTime from = (settlements == null || settlements.isEmpty())
                ? project.getCreatedAt()
                : settlementRepository.findTopByProjectOrderByPeriodEndDesc(project)
                        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.SETTLEMENT_NOT_FOUND))
                        .getPeriodEnd().plusNanos(1);

        LocalDateTime to = LocalDateTime.now();
        List<Order> orders = orderService.findByProjectAndCreatedAtBetween(project, from, to, TossPaymentStatus.DONE);

        long totalAmount = orders.stream().mapToLong(Order::getPaidAmount).sum();
        double feeRate = project.getPricingPlan().getPaymentFee() / 100.0;
        long fee = Math.round(totalAmount * feeRate);
        long payout = totalAmount - fee;

        return mapper.map(project, from, to, totalAmount, fee, payout);
    }


    //월 별 정산 금액
    public SettlementMonthlyTotalResponseDto getPurchaseSettlementTotalByMonth(YearMonth yearMonth){
        User user  = userService.findUserById(tokenService.getUserIdFromAccessToken());

        // 1. yearMonth 이전 달 계산
        YearMonth prevMonth = yearMonth.minusMonths(1);


        // 2. 시작: 이전 달의 20일 15시
        LocalDateTime start = prevMonth.atDay(20).atTime(15, 0);

        // 3. 끝: 해당 월의 20일 15시
        LocalDateTime end = yearMonth.atDay(20).atTime(15, 0);

        List<Settlement> data =  settlementRepository.findAllByUserAndSettledAtBetween(user, start, end);

        return aggregateMonthlySettlement(data,yearMonth);
    }

    private SettlementMonthlyTotalResponseDto aggregateMonthlySettlement(List<Settlement> settlements, YearMonth yearMonth) {
        long totalOrderAmount = 0L;
        long feeAmount = 0L;
        long payoutAmount = 0L;

        for (Settlement settlement : settlements) {
            totalOrderAmount += (settlement.getTotalOrderAmount() != null) ? settlement.getTotalOrderAmount() : 0L;
            feeAmount += (settlement.getFeeAmount() != null) ? settlement.getFeeAmount() : 0L;
            payoutAmount += (settlement.getPayoutAmount() != null) ? settlement.getPayoutAmount() : 0L;
        }

        return new SettlementMonthlyTotalResponseDto(
                yearMonth.getYear(),
                yearMonth.getMonthValue(),
                totalOrderAmount,
                feeAmount,
                payoutAmount
        );
    }




}
