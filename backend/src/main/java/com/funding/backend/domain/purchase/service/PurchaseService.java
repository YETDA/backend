package com.funding.backend.domain.purchase.service;

import com.funding.backend.domain.project.dto.request.ProjectCreateRequestDto;
import com.funding.backend.domain.project.dto.request.PurchaseProjectDetail;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.purchase.repository.PurchaseRepository;
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

    @Transactional
    public void createPurchase(Project project, PurchaseProjectDetail dto){
        String fileUrl = "";
        Purchase purchase = Purchase.builder()
                .project(project)
                .file(fileUrl)
                .averageDeliveryTime("")
                .gitAddress(dto.getGitAddress())
                .providingMethod(dto.getProvidingMethod())
                .build();
        purchaseRepository.save(purchase);
    }
}
