package com.funding.backend.domain.project.service;

import com.funding.backend.domain.donation.service.DonationService;
import com.funding.backend.domain.pricingPlan.repository.PricingRepository;
import com.funding.backend.domain.pricingPlan.service.PricingService;
import com.funding.backend.domain.project.dto.request.ProjectCreateRequestDto;
import com.funding.backend.domain.project.dto.response.ProjectResponseDto;
import com.funding.backend.domain.project.dto.response.PurchaseProjectResponseDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.domain.purchase.dto.request.PurchaseOptionRequestDto;
import com.funding.backend.domain.purchase.dto.request.PurchaseProjectDetail;
import com.funding.backend.domain.purchase.dto.request.PurchaseUpdateRequestDto;
import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.purchase.service.PurchaseService;
import com.funding.backend.domain.purchaseCategory.entity.PurchaseCategory;
import com.funding.backend.domain.purchaseCategory.service.PurchaseCategoryService;
import com.funding.backend.domain.purchaseOption.entity.PurchaseOption;
import com.funding.backend.domain.purchaseOption.service.PurchaseOptionService;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.enums.ProvidingMethod;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.utils.s3.ImageService;
import com.funding.backend.global.utils.s3.S3FileInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final PurchaseOptionService purchaseOptionService;


    @Transactional
    public void createPurchaseProject(ProjectCreateRequestDto dto){

        Optional<User> user = userRepository.findById(Long.valueOf(1));
        Project project = Project.builder()
                .introduce(dto.getIntroduce())
                .title(dto.getTitle())
                .content(dto.getContent())
                .projectStatus(ProjectStatus.UNDER_REVIEW) //처음 만들때는 심사중으로
                .pricingPlan(pricingService.findById(dto.getPricingPlanId()))
                .projectType(ProjectType.PURCHASE)
                .user(user.get())
                .build();

        //여기서도 이미지가 존재하는 경우만 저장되게
        Project saveProject = projectRepository.save(project);

        List<ProjectImage> projectImage = new ArrayList<>();
        if (dto.getContentImage() != null && !dto.getContentImage().isEmpty()) {
            projectImage = imageService.saveImageList(dto.getContentImage(), project);
        }
        project.setProjectImage(projectImage);


        //구매 프로젝트 정보 저장
        Purchase createPurchase = purchaseService.createPurchase(saveProject,dto.getPurchaseDetail());
        // 옵션 저장
        if (dto.getPurchaseDetail().getPurchaseOptionList() != null && !dto.getPurchaseDetail().getPurchaseOptionList().isEmpty()) {
            purchaseOptionService.createPurchaseOptionForProject(createPurchase.getId(),dto);
        }

    }


    @Transactional
    public void  updatePurchaseProject(Long projectId, PurchaseUpdateRequestDto purchaseUpdateRequestDto) {
        Project project = findProjectById(projectId);
        // 권한 체크로 -> 로그인 완료되면 구현
        //validProjectUser(project.getUser(), loginUser);


        // 프로젝트 기본 필드 수정
        project.setTitle(purchaseUpdateRequestDto.getTitle());
        project.setIntroduce(purchaseUpdateRequestDto.getIntroduce());

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

    public ProjectResponseDto getProjectDetail(Long projectId) {
        Project project = findProjectById(projectId);

        if (project.getProjectType() == ProjectType.PURCHASE) {
            return purchaseService.createPurchaseProjectResponse(project);
//        } else if (project.getProjectType() == ProjectType.DONATION) {
//            return createDonationProjectResponse(project);
        } else {
            throw new BusinessLogicException(ExceptionCode.INVALID_PROJECT_TYPE);
        }
    }


    @Transactional
    public void deleteProject(Long projectId) {
        //삭제 하려는 유저가 본인인지 확인하는 로직 필요
        //validProjectUser(project.getUser(), getCurrentUser());

        Project project = findProjectById(projectId);
        projectRepository.delete(project);
    }


}
