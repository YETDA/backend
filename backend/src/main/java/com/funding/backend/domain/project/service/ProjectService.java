package com.funding.backend.domain.project.service;

import com.funding.backend.domain.donation.service.DonationService;
import com.funding.backend.domain.pricingPlan.repository.PricingRepository;
import com.funding.backend.domain.pricingPlan.service.PricingService;
import com.funding.backend.domain.project.dto.request.ProjectCreateRequestDto;
import com.funding.backend.domain.project.dto.response.PurchaseProjectResponseDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.domain.purchase.dto.request.PurchaseUpdateRequestDto;
import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.purchase.service.PurchaseService;
import com.funding.backend.domain.purchaseCategory.entity.PurchaseCategory;
import com.funding.backend.domain.purchaseCategory.service.PurchaseCategoryService;
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
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final DonationService donationService;
    private final PricingRepository pricingRepository;

    private final PricingService pricingService;
    private final PurchaseCategoryService purchaseCategoryService;
    private final ImageService imageService;
    private final PurchaseService purchaseService;
    private final UserRepository userRepository;


    @Transactional
    public void createPurchaseProject(ProjectCreateRequestDto dto){
        List<ProjectImage> projectImage = new ArrayList<>();
        PurchaseCategory purchaseCategory = purchaseCategoryService.findPurchaseCategoryById(dto.getPurchaseDetail().getPurchaseCategoryId());
        String coverImage = "";
        Optional<User> user = userRepository.findById(Long.valueOf(2));
        Project project = Project.builder()
                .purchaseCategory(purchaseCategory)
                .introduce(dto.getIntroduce())
                .title(dto.getTitle())
                .content(dto.getContent())
                .projectImage(projectImage)
                .coverImage(coverImage)
                .projectStatus(ProjectStatus.UNDER_REVIEW) //처음 만들때는 심사중으로
                .pricingPlan(pricingService.findById(dto.getPricingPlanId()))
                .projectType(ProjectType.PURCHASE)
                .user(user.get())
                .build();
        Project saveProject = projectRepository.save(project);

        imageService.saveImageList(dto.getContentImage(),saveProject);
        purchaseService.createPurchase(saveProject,dto.getPurchaseDetail());
    }

    @Transactional
    public void  updatePurchaseProject(Long projectId, PurchaseUpdateRequestDto purchaseUpdateRequestDto) {
        Project project = findProjectById(projectId);
        PurchaseCategory purchaseCategory = purchaseCategoryService.findPurchaseCategoryById(purchaseUpdateRequestDto.getPurchaseCategoryId());

        // 권한 체크로 -> 로그인 완료되면 구현
        //validProjectUser(project.getUser(), loginUser);


        // 프로젝트 기본 필드 수정
        project.setTitle(purchaseUpdateRequestDto.getTitle());
        project.setIntroduce(purchaseUpdateRequestDto.getIntroduce());
        project.setPurchaseCategory(purchaseCategory);

        // 프로젝트 상세 내용 업데이트
        project.setContent(purchaseUpdateRequestDto.getContent());

        // 이미지 업데이트
        List<ProjectImage> updatedImages = imageService.updateImageList
                (project.getProjectImage(), purchaseUpdateRequestDto.getContentImage(), project);
        project.setProjectImage(updatedImages);
        projectRepository.save(project);
        // Purchase 관련 필드 업데이트
        purchaseService.updatePurchase(project,purchaseUpdateRequestDto);
    }

    public PurchaseProjectResponseDto getPurchaseProject(Long projectId) {
        Project project = findProjectById(projectId);

        Purchase purchase = purchaseService.findByProject(project);

        return new PurchaseProjectResponseDto(project, purchase);
    }

    @Transactional
    public void deletePurchaseProject(Long projectId) {
        Project project = findProjectById(projectId);

//        // 권한 체크 (로그인 유저 필요 시 매개변수 추가)
//        validProjectUser(project.getUser(), getCurrentUser());

        // 연결된 Purchase도 삭제
        purchaseService.deletePurchase(purchaseService.findByProject(project));

        // 프로젝트 삭제
        projectRepository.delete(project);
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
