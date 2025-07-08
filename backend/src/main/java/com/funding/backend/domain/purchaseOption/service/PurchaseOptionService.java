package com.funding.backend.domain.purchaseOption.service;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.purchase.repository.PurchaseRepository;
import com.funding.backend.domain.purchase.service.PurchaseService;
import com.funding.backend.domain.purchaseOption.dto.request.PurchaseOptionCreateRequestDto;
import com.funding.backend.domain.purchaseOption.dto.request.PurchaseOptionUpdateRequestDto;
import com.funding.backend.domain.purchaseOption.entity.PurchaseOption;
import com.funding.backend.domain.purchaseOption.repository.PurchaseOptionRepository;
import com.funding.backend.enums.OptionStatus;
import com.funding.backend.enums.ProvidingMethod;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.utils.s3.ImageService;
import com.funding.backend.global.utils.s3.S3FileInfo;
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
public class PurchaseOptionService {
    private final PurchaseOptionRepository purchaseOptionRepository;
    private final PurchaseRepository purchaseRepository;
    private final ProjectService projectService;
    private final PurchaseService purchaseService;
    private final ImageService imageService;



    @Transactional
    public void createPurchaseOption(Long projectId, PurchaseOptionCreateRequestDto requestDto) {
        Purchase purchase = purchaseService.findByProject(projectService.findProjectById(projectId));

        if (purchase.getProvidingMethod().equals(ProvidingMethod.DOWNLOAD)) {
            createDownloadOption(purchase, requestDto);
        } else if (purchase.getProvidingMethod().equals(ProvidingMethod.EMAIL)) {
            createEmailOption(purchase, requestDto);
        } else {
            throw new BusinessLogicException(ExceptionCode.UNSUPPORTED_PROVIDING_METHOD);
        }
    }

    @Transactional
    public void updatePurchaseOption(Long purchaseOptionId, PurchaseOptionUpdateRequestDto requestDto) {
        PurchaseOption purchaseOption = findPurchaseOptionById(purchaseOptionId);

        if (requestDto.getFile() != null) {
            updateFileIfChanged(purchaseOption, requestDto.getFile());
        }

        Optional.ofNullable(requestDto.getTitle())
                .ifPresent(purchaseOption::setTitle);
        Optional.ofNullable(requestDto.getContent())
                .ifPresent(purchaseOption::setContent);
        Optional.ofNullable(requestDto.getOptionStatus())
                .ifPresent(purchaseOption::setOptionStatus);
        Optional.ofNullable(requestDto.getPrice())
                .ifPresent(purchaseOption::setPrice);

        purchaseOptionRepository.save(purchaseOption);
    }


    public PurchaseOption findPurchaseOptionById(Long purchaseOptionId){
        return purchaseOptionRepository.findById(purchaseOptionId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.PURCHASE_OPTION_NOT_FOUND));
    }
    private void createDownloadOption(Purchase purchase, PurchaseOptionCreateRequestDto requestDto) {
        S3FileInfo fileData = imageService.saveFile(requestDto.getFile());
        // 파일이 없으면 예외 던지기
        if (requestDto.getFile() == null || requestDto.getFile().isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.FILE_REQUIRED_FOR_DOWNLOAD_OPTION);

        }
        PurchaseOption option = PurchaseOption.builder()
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
        purchaseOptionRepository.save(option);
    }

    private void createEmailOption(Purchase purchase, PurchaseOptionCreateRequestDto requestDto) {
        PurchaseOption option = PurchaseOption.builder()
                .optionStatus(requestDto.getOptionStatus())
                .price(requestDto.getPrice())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .purchase(purchase)
                .build();
        purchaseOptionRepository.save(option);
    }


    private void updateFileIfChanged(PurchaseOption purchaseOption, MultipartFile newFile) {
        String currentFileHash = generateFileHash(
                purchaseOption.getOriginalFileName(),
                purchaseOption.getFileSize(),
                purchaseOption.getFileType()
        );
        String newFileHash = generateFileHash(newFile);

        if (!currentFileHash.equals(newFileHash)) {
            imageService.deleteImage(purchaseOption.getFileUrl());
            S3FileInfo fileInfo = imageService.saveFile(newFile);

            purchaseOption.setFileSize(fileInfo.fileSize());
            purchaseOption.setFileType(fileInfo.fileType());
            purchaseOption.setFileUrl(fileInfo.fileUrl());
            purchaseOption.setOriginalFileName(fileInfo.originalFileName());
        }
    }


    private String generateFileHash(String originalFileName, long fileSize, String fileType) {
        return imageService.generateFileHash(originalFileName, fileSize, fileType);
    }

    private String generateFileHash(MultipartFile file) {
        return imageService.generateFileHash(
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType()
        );
    }




}
