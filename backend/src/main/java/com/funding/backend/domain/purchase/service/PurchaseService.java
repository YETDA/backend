package com.funding.backend.domain.purchase.service;

import com.funding.backend.domain.project.dto.response.PurchaseOptionResponseDto;
import com.funding.backend.domain.project.dto.response.PurchaseProjectResponseDto;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.purchase.dto.request.PurchaseProjectDetail;
import com.funding.backend.domain.purchase.dto.request.PurchaseUpdateRequestDto;
import com.funding.backend.domain.purchaseCategory.entity.PurchaseCategory;
import com.funding.backend.domain.purchaseCategory.service.PurchaseCategoryService;
import com.funding.backend.domain.purchaseOption.entity.PurchaseOption;
import com.funding.backend.domain.purchase.dto.request.PurchaseOptionRequestDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.purchase.repository.PurchaseRepository;
import com.funding.backend.domain.purchaseOption.repository.PurchaseOptionRepository;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.utils.s3.ImageService;
import com.funding.backend.global.utils.s3.S3FileInfo;
import com.funding.backend.global.utils.s3.S3Uploader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Transactional
    public void createPurchase(Project project, PurchaseProjectDetail dto){
        PurchaseCategory purchaseCategory = purchaseCategoryService.findPurchaseCategoryById(dto.getPurchaseCategoryId());

        Purchase purchase = Purchase.builder()
                .project(project)
                .averageDeliveryTime(dto.getGetAverageDeliveryTime())
                .gitAddress(dto.getGitAddress())
                .providingMethod(dto.getProvidingMethod())
                .purchaseCategory(purchaseCategory)
                .build();
        Purchase savePurchase = purchaseRepository.save(purchase);

        // 옵션 저장
        if (dto.getPurchaseOptionList() != null && !dto.getPurchaseOptionList().isEmpty()) {
            for (PurchaseOptionRequestDto optionDto : dto.getPurchaseOptionList()) {
                PurchaseOption option = PurchaseOption.builder()
                        .purchase(savePurchase)
                        .title(optionDto.getTitle())
                        .content(optionDto.getContent())
                        .fileUrl(optionDto.getFileUrl())
                        .price(optionDto.getPrice())
                        .optionStatus(optionDto.getOptionStatus())
                        .build();
                purchaseOptionRepository.save(option);
            }
        }
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

        Optional.ofNullable(dto.getProvidingMethod())
                .ifPresent(purchase::setProvidingMethod);

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


    //파일 이름을, dto에 저장 할 수 있게
    public void matchOptionFilesToDto(
            List<PurchaseOptionRequestDto> purchaseOptionRequestDto,
            List<MultipartFile> files
    ) {
        if (purchaseOptionRequestDto == null || files == null) return;

        if (purchaseOptionRequestDto.size() != files.size()) {
            throw new BusinessLogicException(ExceptionCode.PURCHASE_OPTION_FILE_COUNT);
        }

        for (int i = 0; i < purchaseOptionRequestDto.size(); i++) {
            MultipartFile file = files.get(i);
            S3FileInfo fileInfo = imageService.saveFile(file);

            PurchaseOptionRequestDto option = purchaseOptionRequestDto.get(i);
            option.setFileUrl(fileInfo.fileUrl());

        }
    }

    public PurchaseProjectResponseDto createPurchaseProjectResponse(Project project) {
        Purchase detail = findByProject(project);

        List<PurchaseOptionResponseDto> optionDtos = detail.getPurchaseOptionList().stream()
                .map(PurchaseOptionResponseDto::new).toList();

        return new PurchaseProjectResponseDto(
               project,detail,optionDtos
        );
    }



}
