package com.funding.backend.domain.project.service;

import com.funding.backend.domain.donation.service.DonationService;
import com.funding.backend.domain.pricingPlan.entity.PricingPlan;
import com.funding.backend.domain.pricingPlan.repository.PricingRepository;
import com.funding.backend.domain.project.dto.request.ProjectCreateRequestDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.domain.purchase.service.PurchaseService;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import java.util.ArrayList;
import java.util.List;
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
    private final PricingRepository pricingRepository;


    @Transactional
    public void createPurchaseProject(ProjectCreateRequestDto dto){
        List<ProjectImage> projectImage = new ArrayList<>();
        String coverImage = "";
        Project project = Project.builder()
                .purchaseCategory(dto.getPurchaseDetail().getPurchaseCategory())
                .introduce(dto.getIntroduce())
                .title(dto.getTitle())
                .projectImage(projectImage)
                .coverImage(coverImage)
                .projectStatus(ProjectStatus.UNDER_REVIEW) //처음 만들때는 심사중으로
                .pricingPlan(pricingRepository.findById(dto.getPricingPlanId()));

                .build();



    }



}
