package com.funding.backend.domain.settlement.service;

import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.order.service.OrderService;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.settlement.dto.response.SettlementDetailListResponseDto;
import com.funding.backend.domain.settlement.dto.response.SettlementDetailResponseDto;
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

    // 관리자용: 특정 사용자의 정산 요청(Settlement) 총 개수
    public long countByUser(Long userId) {
        return settlementRepository.countByUserId(userId);
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
}
