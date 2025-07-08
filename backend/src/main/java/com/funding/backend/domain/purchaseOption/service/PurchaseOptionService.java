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
        MultipartFile optionFile = requestDto.getFile();
        String fileUrl = imageService.

        PurchaseOption purchaseOption = PurchaseOption.builder()
                .optionStatus(requestDto.getOptionStatus())
                .price(requestDto.getPrice())
                .content(requestDto.getContent())
                .fileSize(optionFile.getSize())
                .originalFileName(optionFile.getOriginalFilename())
                .fileType(optionFile.getContentType())
                .build();


    }








}
