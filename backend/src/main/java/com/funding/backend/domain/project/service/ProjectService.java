package com.funding.backend.domain.project.service;

import com.funding.backend.domain.donation.service.DonationService;
import com.funding.backend.domain.project.dto.request.ProjectCreateRequestDto;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.purchase.service.PurchaseService;
import com.funding.backend.enums.ProjectType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final PurchaseService purchaseService;
    private final DonationService donationService;

    public void createProject(ProjectCreateRequestDto dto) {
        if (dto.getProjectType() == ProjectType.DONATION) {
            createDonationProject(dto);
        } else if (dto.getProjectType() == ProjectType.PURCHASE) {
            createPurchaseProject(dto);
        } else {
            throw new IllegalArgumentException("잘못된 프로젝트 타입입니다.");
        }
    }



}
