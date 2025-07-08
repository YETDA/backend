package com.funding.backend.domain.purchase.service;

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
import com.funding.backend.domain.purchaseOption.repository.PurchaseOptionRepository;
import com.funding.backend.domain.purchaseOption.service.PurchaseOptionService;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.utils.s3.ImageService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseOptionRepository purchaseOptionRepository;
    private final ImageService imageService;
    private final ProjectRepository projectRepository;
    private final PurchaseCategoryService purchaseCategoryService;
    private final PurchaseOptionService purchaseOptionService;

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
        Purchase detail = findByProject(project);

        List<PurchaseOptionResponseDto> optionDtos = detail.getPurchaseOptionList().stream()
                .map(PurchaseOptionResponseDto::new).toList();

        return new PurchaseProjectResponseDto(
                project, detail, optionDtos
        );
    }





}
