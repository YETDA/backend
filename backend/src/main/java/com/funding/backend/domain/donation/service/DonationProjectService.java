package com.funding.backend.domain.donation.service;

import com.funding.backend.domain.donationCategory.entity.DonationCategory;
import com.funding.backend.domain.donationCategory.service.DonationCategoryService;
import com.funding.backend.domain.pricingPlan.service.PricingService;
import com.funding.backend.domain.project.dto.request.ProjectCreateRequestDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.utils.s3.ImageService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DonationProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private final DonationService donationService;
    private final ImageService imageService;
    private final PricingService pricingService;
    private final DonationCategoryService donationCategoryService;


    @Transactional
    public void createDonationProject(ProjectCreateRequestDto dto){
        List<ProjectImage> projectImage = new ArrayList<>();
        DonationCategory donationCategory = donationCategoryService.findDonationCategoryById(dto.getDonationDetail().getMainCategoryId());
        String coverImage = "";
        Optional<User> user = userRepository.findById(Long.valueOf(2));
        Project project = Project.builder()
                .donationCategory(donationCategory)
                .introduce(dto.getIntroduce())
                .title(dto.getTitle())
                .content(dto.getContent())
                .projectImage(projectImage)
                .coverImage(coverImage)
                .projectStatus(ProjectStatus.UNDER_REVIEW) //처음 만들때는 심사중으로
                .pricingPlan(pricingService.findById(dto.getPricingPlanId()))
                .projectType(ProjectType.DONATION)
                .user(user.get())
                .build();
        Project saveProject = projectRepository.save(project);

        imageService.saveImageList(dto.getContentImage(),saveProject);
        donationService.createDonation(saveProject,dto.getDonationDetail());
    }

}
