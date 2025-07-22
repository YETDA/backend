package com.funding.backend.domain.purchase.service;

import com.funding.backend.domain.follow.service.FollowService;
import com.funding.backend.domain.order.service.OrderService;
import com.funding.backend.domain.project.dto.response.ProjectInfoResponseDto;
import com.funding.backend.domain.purchase.dto.response.PurchaseInfoResponseDto;
import com.funding.backend.domain.purchase.dto.response.PurchaseListResponseDto;
import com.funding.backend.domain.purchaseOption.dto.response.PurchaseOptionResponseDto;
import com.funding.backend.domain.project.dto.response.PurchaseProjectResponseDto;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.purchase.dto.request.PurchaseProjectDetail;
import com.funding.backend.domain.purchase.dto.request.PurchaseUpdateRequestDto;
import com.funding.backend.domain.purchaseCategory.entity.PurchaseCategory;
import com.funding.backend.domain.purchaseCategory.service.PurchaseCategoryService;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.purchase.repository.PurchaseRepository;
import com.funding.backend.domain.purchaseOption.service.PurchaseOptionService;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.TokenService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProjectRepository projectRepository;
    private final PurchaseCategoryService purchaseCategoryService;

    private final OrderService orderService;
    private final PurchaseOptionService purchaseOptionService;
    private final UserService userService;
    private final TokenService tokenService;
    private final FollowService followService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Purchase createPurchase(Project project, PurchaseProjectDetail dto){
        PurchaseCategory purchaseCategory = purchaseCategoryService.findPurchaseCategoryById(dto.getPurchaseCategoryId());

        Purchase purchase = Purchase.builder()
                .project(project)
                .averageDeliveryTime(dto.getGetAverageDeliveryTime())
                .gitAddress(dto.getGitAddress())
                .purchaseCategory(purchaseCategory)
                .build();
        return purchaseRepository.save(purchase);
    }

    @Transactional
    public void updatePurchase(Project project, PurchaseUpdateRequestDto dto) {
        Purchase purchase = findByProject(project);

        if (dto.getPurchaseCategoryId() != null) {
            PurchaseCategory category = purchaseCategoryService.findPurchaseCategoryById(dto.getPurchaseCategoryId());
            purchase.setPurchaseCategory(category);
        }
        Optional.ofNullable(dto.getGitAddress())
                .ifPresent(purchase::setGitAddress);


        Optional.ofNullable(dto.getAverageDeliveryTime())
                .ifPresent(purchase::setAverageDeliveryTime);

        purchaseRepository.save(purchase);

    }

    @Transactional
    public void deletePurchase(Long projectId){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.PROJECT_NOT_FOUND));

        Purchase purchase = purchaseRepository.findById(findByProject(project).getId())
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.PURCHASE_NOT_FOUND));

        purchaseRepository.delete(purchase);
    }


    public Purchase findByProject(Project project){
        return purchaseRepository.findByProject(project)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PURCHASE_NOT_FOUND));
    }
    public Purchase findByProject(Long purchaseId ){
        return purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PURCHASE_NOT_FOUND));
    }



    public PurchaseProjectResponseDto createPurchaseProjectResponse(Project project) {

        if(project.getProjectStatus().equals(ProjectStatus.UNDER_AUDIT)){
            User user = userService.findUserById(tokenService.getUserIdFromAccessToken());
            if(!project.getUser().equals(user)){
                throw new BusinessLogicException(ExceptionCode.PROJECT_VIEW_FORBIDDEN_DURING_AUDIT);
            }
        }
        Purchase detail = findByProject(project);

        List<PurchaseOptionResponseDto> optionDtos = detail.getPurchaseOptionList().stream()
                .map(PurchaseOptionResponseDto::new).toList();
        User user = userService.findUserById(tokenService.getUserIdFromAccessToken());
        Long projectCount = projectRepository.countByUserIdAndProjectStatusIn(user.getId(), Arrays.asList(ProjectStatus.RECRUITING, ProjectStatus.COMPLETED));
        Long followerCount = followService.countFollowers(user.getId());

        //구매 프로젝트 승인 알림 생성
        return new PurchaseProjectResponseDto(
                project, detail, optionDtos , projectCount, followerCount
        );

    }



    //구매자 검증을 위한 조회 메서드(후기)
    public Purchase findByIdWithProjectAndUser(Long purchaseId) {
        return purchaseRepository.findByIdWithProjectAndUser(purchaseId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PURCHASE_NOT_FOUND));
    }


    public Page<PurchaseListResponseDto> getMyPurchaseProjectList(Pageable pageable){
        User loginUser = userService.findUserById(tokenService.getUserIdFromAccessToken());
        Page<Project> purchaseProjectList = projectRepository.findByUserIdAndProjectType(
                loginUser.getId(), ProjectType.PURCHASE, pageable);

        return purchaseProjectList.map(project -> {
            Long sellCount = orderService.purchaseOrderCount(project);
            return new PurchaseListResponseDto(project, sellCount);
        });
    }


    public Page<PurchaseInfoResponseDto> getPurchaseCategoryProjectList(
            Long categoryId, Pageable pageable, ProjectStatus projectStatus) {

        PurchaseCategory purchaseCategory = purchaseCategoryService.findPurchaseCategoryById(categoryId);

        Page<Project> projectPage = projectRepository.findByPurchaseCategoryAndStatus(
                purchaseCategory.getId(), projectStatus, pageable);

        // 프로젝트 ID 리스트 추출
        List<Long> projectIds = projectPage.stream()
                .map(Project::getId)
                .toList();

        // ID별 판매 수량 Map 조회
        List<Object[]> orderCounts = orderService.countOrdersByProjectIds(projectIds);

        Map<Long, Long> projectIdToSellCount = orderCounts.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        // DTO 변환
        return projectPage.map(project -> {
            Long sellCount = projectIdToSellCount.getOrDefault(project.getId(), 0L);
            return new PurchaseInfoResponseDto(project, sellCount);
        });
    }





}
