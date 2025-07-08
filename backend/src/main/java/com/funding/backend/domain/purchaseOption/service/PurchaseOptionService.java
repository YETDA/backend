package com.funding.backend.domain.purchaseOption.service;

import com.funding.backend.domain.project.dto.request.ProjectCreateRequestDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.purchase.dto.request.PurchaseOptionRequestDto;
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
import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private final ProjectRepository projectRepository;
    private final ImageService imageService;


    @Transactional
    public void createPurchaseOptionForProject(Long purchaseId, ProjectCreateRequestDto optionRequestDto) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.PURCHASE_NOT_FOUND));
        List<PurchaseOptionRequestDto> purchaseOptionList = optionRequestDto.getPurchaseDetail().getPurchaseOptionList();
        List<MultipartFile> optionFiles = optionRequestDto.getOptionFiles();

        // 먼저 DTO와 파일 매핑
        matchOptionFilesToDto(purchaseOptionList, optionFiles);

        // 매핑된 DTO로 옵션 생성
        for (PurchaseOptionRequestDto dto : purchaseOptionList) {
            if (dto.getProvidingMethod() == ProvidingMethod.DOWNLOAD) {
                createDownloadOptionWithProject(purchase, dto);
            } else if (dto.getProvidingMethod() == ProvidingMethod.EMAIL) {
                createEmailOptionWithProject(purchase, dto);
            } else {
                throw new BusinessLogicException(ExceptionCode.UNSUPPORTED_PROVIDING_METHOD);
            }
        }
    }


    @Transactional
    public void createPurchaseOption(Long projectId, PurchaseOptionCreateRequestDto requestDto) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.PROJECT_NOT_FOUND));
        Purchase purchase = purchaseRepository.findByProject(project)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.PURCHASE_NOT_FOUND));

        if (requestDto.getProvidingMethod().equals(ProvidingMethod.DOWNLOAD)) {
            createDownloadOption(purchase, requestDto);
        } else if (requestDto.getProvidingMethod().equals(ProvidingMethod.EMAIL)) {
            createEmailOption(purchase, requestDto);
        } else {
            throw new BusinessLogicException(ExceptionCode.UNSUPPORTED_PROVIDING_METHOD);
        }
    }

    @Transactional
    public void updatePurchaseOption(Long purchaseOptionId, PurchaseOptionUpdateRequestDto requestDto) {
        PurchaseOption purchaseOption = findPurchaseOptionById(purchaseOptionId);

        ProvidingMethod method = requestDto.getProvidingMethod();
        if (requestDto.getProvidingMethod() == null) {
            throw new BusinessLogicException(ExceptionCode.UNSUPPORTED_PROVIDING_METHOD);
        }

        // 공통 필드 업데이트
        Optional.ofNullable(requestDto.getTitle())
                .ifPresent(purchaseOption::setTitle);
        Optional.ofNullable(requestDto.getContent())
                .ifPresent(purchaseOption::setContent);
        Optional.ofNullable(requestDto.getOptionStatus())
                .ifPresent(purchaseOption::setOptionStatus);
        Optional.ofNullable(requestDto.getPrice())
                .ifPresent(purchaseOption::setPrice);

        // DOWNLOAD일 때만 파일 처리
        if (method.equals(ProvidingMethod.DOWNLOAD) && requestDto.getFile() != null) {
            updateFileIfChanged(purchaseOption, requestDto.getFile());
        }

        purchaseOptionRepository.save(purchaseOption);
    }



    public PurchaseOption findPurchaseOptionById(Long purchaseOptionId){
        return purchaseOptionRepository.findById(purchaseOptionId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.PURCHASE_OPTION_NOT_FOUND));
    }

    private void createDownloadOption(Purchase purchase, PurchaseOptionCreateRequestDto requestDto) {
        S3FileInfo fileData = imageService.saveFile(requestDto.getFile());
        // ✅ 파일이 없으면 예외 던지기
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
                .providingMethod(requestDto.getProvidingMethod())
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
                .providingMethod(requestDto.getProvidingMethod())
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

    private void createDownloadOptionWithProject(Purchase purchase, PurchaseOptionRequestDto requestDto) {
        PurchaseOption option = PurchaseOption.builder()
                .optionStatus(requestDto.getOptionStatus())
                .price(requestDto.getPrice())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .fileSize(requestDto.getFileSize())
                .originalFileName(requestDto.getOriginalFileName())
                .fileType(requestDto.getFileType())
                .fileUrl(requestDto.getFileUrl())
                .providingMethod(requestDto.getProvidingMethod())
                .purchase(purchase)
                .build();
        purchaseOptionRepository.save(option);
    }



    private void createEmailOptionWithProject(Purchase purchase, PurchaseOptionRequestDto requestDto) {
        PurchaseOption option = PurchaseOption.builder()
                .optionStatus(requestDto.getOptionStatus())
                .price(requestDto.getPrice())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .providingMethod(requestDto.getProvidingMethod())
                .purchase(purchase)
                .build();
        purchaseOptionRepository.save(option);
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

    //파일 이름을, dto에 저장 할 수 있게
    public void matchOptionFilesToDto(
            List<PurchaseOptionRequestDto> purchaseOptionRequestDto,
            List<MultipartFile> files
    ) {
        if (purchaseOptionRequestDto == null || files == null) return;

        // "DOWNLOAD" 상태인 옵션만 필터링
        long downloadOptionCount = purchaseOptionRequestDto.stream()
                .filter(option -> ProvidingMethod.DOWNLOAD.equals(option.getProvidingMethod()))
                .count();

        // 파일 개수와 DOWNLOAD 옵션 개수 비교
        if (downloadOptionCount != files.size()) {
            throw new BusinessLogicException(ExceptionCode.PURCHASE_OPTION_FILE_COUNT);
        }

        // 업로드된 파일을 이름으로 매핑
        Map<String, MultipartFile> fileMap = files.stream()
                .filter(file -> file.getOriginalFilename() != null)
                .collect(Collectors.toMap(
                        file -> Normalizer.normalize(file.getOriginalFilename(), Normalizer.Form.NFC),
                        f -> f
                ));


        for (PurchaseOptionRequestDto option : purchaseOptionRequestDto) {
            if (!ProvidingMethod.DOWNLOAD.equals(option.getProvidingMethod())) {
                continue;
            }
            String identifier = option.getFileIdentifier();

            if (identifier == null || !fileMap.containsKey(Normalizer.normalize(identifier, Normalizer.Form.NFC))) {
                log.warn("❗ 파일 식별자 미일치: identifier = [{}], 파일 목록 = {}",
                        identifier, fileMap.keySet());
                throw new BusinessLogicException(ExceptionCode.PURCHASE_OPTION_FILE_NOT_FOUND);
            }


            MultipartFile matchedFile = fileMap.get(identifier);
            S3FileInfo fileInfo = imageService.saveFile(matchedFile);
            option.setFileUrl(fileInfo.fileUrl());
            option.setFileSize(fileInfo.fileSize());
            option.setFileType(fileInfo.fileType());
            option.setOriginalFileName(fileInfo.originalFileName());
        }
    }




}
