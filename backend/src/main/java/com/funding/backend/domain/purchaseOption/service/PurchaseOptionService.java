package com.funding.backend.domain.purchaseOption.service;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.purchase.repository.PurchaseRepository;
import com.funding.backend.domain.purchase.service.PurchaseService;
import com.funding.backend.domain.purchaseOption.dto.request.PurchaseOptionCreateRequestDto;
import com.funding.backend.domain.purchaseOption.entity.PurchaseOption;
import com.funding.backend.domain.purchaseOption.repository.PurchaseOptionRepository;
import com.funding.backend.global.utils.s3.ImageService;
import com.funding.backend.global.utils.s3.S3FileInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PurchaseOptionService {
    private final PurchaseOptionRepository purchaseOptionRepository;
    private final PurchaseRepository purchaseRepository;
    private final ProjectService projectService;
    private final PurchaseService purchaseService;
    private final ImageService imageService;



    @Transactional
    public void createPurchaseProject(Long projectId, PurchaseOptionCreateRequestDto requestDto){
        Purchase purchase = purchaseService.findByProject(projectService.findProjectById(projectId));
        S3FileInfo fileData = imageService.saveFile(requestDto.getFile());

        PurchaseOption purchaseOption = PurchaseOption.builder()
                .optionStatus(requestDto.getOptionStatus())
                .price(requestDto.getPrice())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .fileSize(fileData.fileSize())
                .originalFileName(fileData.originalFileName())
                .fileType(fileData.fileType())
                .fileUrl(fileData.fileUrl())
                .purchase(purchase)
                .build();
        purchaseOptionRepository.save(purchaseOption);
    }








}
