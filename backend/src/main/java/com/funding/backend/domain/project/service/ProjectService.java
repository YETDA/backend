package com.funding.backend.domain.project.service;

import com.funding.backend.domain.donation.service.DonationService;
import com.funding.backend.domain.pricingPlan.entity.PricingPlan;
import com.funding.backend.domain.pricingPlan.repository.PricingRepository;
import com.funding.backend.domain.pricingPlan.service.PricingService;
import com.funding.backend.domain.project.dto.request.ProjectCreateRequestDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.domain.purchase.service.PurchaseService;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
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
    private final PricingService pricingService;


    @Transactional
    public void createPurchaseProject(ProjectCreateRequestDto dto){
        List<ProjectImage> projectImage = new ArrayList<>();
        String coverImage = "";
        User user = new User();
        Project project = Project.builder()
                .purchaseCategory(dto.getPurchaseDetail().getPurchaseCategory())
                .introduce(dto.getIntroduce())
                .title(dto.getTitle())
                .projectImage(projectImage)
                .coverImage(coverImage)
                .projectStatus(ProjectStatus.UNDER_REVIEW) //처음 만들때는 심사중으로
                .pricingPlan(pricingService.findById(dto.getPricingPlanId()))
                .projectType(ProjectType.PURCHASE)
                .user(user)
                .build();
        Project saveProject = projectRepository.save(project);
        purchaseService.createPurchase(saveProject,dto.getPurchaseDetail());
    }

    @Transactional
    public Project updateProject(Long projectId, ProjectCreateRequestDto dto, User loginUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PROJECT_NOT_FOUND));

        // 권한 체크
        validProjectUser(project.getUser(), loginUser);

        // 전체 필드 덮어쓰기
        project.setTitle(dto.getTitle());
        project.setIntroduce(dto.getIntroduce());
        project.setCoverImage(""); // 실제 업로드된 커버 이미지가 있다면 dto로부터 가져와서 설정
        project.setProjectImage(new ArrayList<>()); // 추후 이미지 DTO에서 변환
        project.setProjectStatus(ProjectStatus.UNDER_REVIEW); // 수정 시에도 초기화할지 여부는 정책에 따라 조절
        project.setPricingPlan(pricingService.findById(dto.getPricingPlanId()));
        project.setProjectType(ProjectType.PURCHASE); // 고정값
        project.setPurchaseCategory(dto.getPurchaseDetail().getPurchaseCategory());

        // Purchase도 함께 수정
        purchaseService.updatePurchase(project, dto.getPurchaseDetail());

        return projectRepository.save(project);
    }


    public Project findProjectById(Long id){
        return projectRepository.findById(id).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.PROJECT_NOT_FOUND)
        );
    }

    public void validProjectUser(User projectUser, User loginUser){
        if (!projectUser.equals(loginUser)) {
            throw new BusinessLogicException(ExceptionCode.NOT_PROJECT_CREATOR);
        }
    }





}
