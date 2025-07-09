package com.funding.backend.domain.donation.service;

import com.funding.backend.domain.donation.dto.request.DonationCreateRequestDto;
import com.funding.backend.domain.donation.dto.request.DonationUpdateRequestDto;
import com.funding.backend.domain.mainCategory.entity.MainCategory;
import com.funding.backend.domain.mainCategory.service.MainCategoryService;
import com.funding.backend.domain.pricingPlan.service.PricingService;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
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
    private final MainCategoryService mainCategoryService;


    @Transactional
    public void createDonationProject(DonationCreateRequestDto dto){
        List<ProjectImage> projectImage = new ArrayList<>();
        MainCategory donationCategory = mainCategoryService.findDonationCategoryById(dto.getDonationDetail().getMainCategoryId());
        String coverImage = "";
        Optional<User> user = userRepository.findById(Long.valueOf(2));
        Project project = Project.builder()
                .mainCategory(donationCategory)
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
        donationService.createDonation(saveProject, dto.getDonationDetail());

    }

}
