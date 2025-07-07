package com.funding.backend.domain.purchase.service;

import com.funding.backend.domain.purchase.dto.request.PurchaseProjectDetail;
import com.funding.backend.domain.purchaseOption.entity.PurchaseOption;
import com.funding.backend.domain.purchase.dto.request.PurchaseOptionDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.purchase.repository.PurchaseRepository;
import com.funding.backend.domain.purchaseOption.repository.PurchaseOptionRepository;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
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

    @Transactional
    public void createPurchase(Project project, PurchaseProjectDetail dto){
        String fileUrl = "";
        Purchase purchase = Purchase.builder()
                .project(project)
                .file(fileUrl)
                .averageDeliveryTime(dto.getGetAverageDeliveryTime())
                .gitAddress(dto.getGitAddress())
                .providingMethod(dto.getProvidingMethod())
                .build();

        // 옵션 저장
        if (dto.getOptions() != null && !dto.getOptions().isEmpty()) {
            for (PurchaseOptionDto optionDto : dto.getOptions()) {
                PurchaseOption option = PurchaseOption.builder()
                        .purchase(purchase)
                        .title(optionDto.getTitle())
                        .content(optionDto.getContent())
                        .price(optionDto.getPrice())
                        .optionStatus(optionDto.getOptionStatus())
                        .build();
                purchaseOptionRepository.save(option);
            }
        }
        purchaseRepository.save(purchase);
    }

    @Transactional
    public void updatePurchase(Project project, PurchaseProjectDetail dto) {
        Purchase purchase = findByProject(project);
        String file = dto.getFile();

        Optional.ofNullable(dto.getGitAddress())
                .ifPresent(purchase::setGitAddress);

        Optional.ofNullable(dto.getProvidingMethod())
                .ifPresent(purchase::setProvidingMethod);

        Optional.ofNullable(dto.getGetAverageDeliveryTime())
                .ifPresent(purchase::setAverageDeliveryTime);


        Optional.ofNullable(file)
                .ifPresent(purchase::setFile);

        purchaseRepository.save(purchase);

    }


    @Transactional
    public void deletePurchase(Purchase purchase){
        purchaseRepository.findById(purchase.getId())
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.PURCHASE_NOT_FOUND));

        purchaseRepository.delete(purchase);
    }


    public Purchase findByProject(Project project){
        return purchaseRepository.findByProject(project)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PURCHASE_NOT_FOUND));
    }

}
