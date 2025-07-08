package com.funding.backend.domain.purchaseOption.service;

import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.purchase.repository.PurchaseRepository;
import com.funding.backend.domain.purchaseOption.dto.request.PurchaseOptionRequestDto;
import com.funding.backend.domain.purchaseOption.entity.PurchaseOption;
import com.funding.backend.domain.purchaseOption.repository.PurchaseOptionRepository;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PurchaseOptionService {
    private final PurchaseOptionRepository purchaseOptionRepository;
    private final PurchaseRepository purchaseRepository;

    @Transactional
    public void updateOptions(Long purchaseId, List<PurchaseOptionRequestDto> newOptions) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PURCHASE_NOT_FOUND));

        // 기존 옵션 삭제
        purchaseOptionRepository.deleteByPurchase(purchase);

        // 새로운 옵션 저장
        for (PurchaseOptionRequestDto dto : newOptions) {
            PurchaseOption option = PurchaseOption.builder()
                    .purchase(purchase)
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .price(dto.getPrice())
                    .optionStatus(dto.getOptionStatus())
                    .build();
            purchaseOptionRepository.save(option);
        }
    }

}
