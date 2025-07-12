package com.funding.backend.domain.donation.service;

import com.funding.backend.domain.donation.dto.request.DonationUpdateRequestDto;
import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.project.dto.request.DonationCreateRequestDto;
import com.funding.backend.domain.pricingPlan.service.PricingService;
import com.funding.backend.domain.project.dto.response.ProjectResponseDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.domain.purchase.service.PurchaseService;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.utils.s3.ImageService;
import com.funding.backend.security.jwt.TokenService;
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

    private final ImageService imageService;
    private final PricingService pricingService;
    private final DonationService donationService;
    private final PurchaseService purchaseService;

    private final TokenService tokenService;


    @Transactional
    public void createDonationProject(DonationCreateRequestDto dto){

        User loginUser = userRepository.findById(tokenService.getUserIdFromAccessToken())
            .orElseThrow(()->new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        Project project = Project.builder()
                .introduce(dto.getIntroduce())
                .title(dto.getTitle())
                .content(dto.getContent())
                .projectStatus(ProjectStatus.UNDER_REVIEW) //처음 만들때는 심사중으로
                .pricingPlan(pricingService.findById(dto.getPricingPlanId()))
                .projectType(ProjectType.DONATION)
                .user(loginUser)
                .build();
        Project saveProject = projectRepository.save(project);

        Donation savedDonation = donationService.createDonation(saveProject, dto.getDonationProjectDetail());
        project.setDonation(savedDonation);

        List<ProjectImage> projectImage = new ArrayList<>();
        if (dto.getContentImage() != null && !dto.getContentImage().isEmpty()) {
            projectImage = imageService.saveImageList(dto.getContentImage(), project);
        }
        project.setProjectImage(projectImage);
    }


    @Transactional
    public void  updateDonationProject(Long projectId, DonationUpdateRequestDto donationUpdateRequestDto) {
        Project project = findProjectById(projectId);

        User loginUser = userRepository.findById(tokenService.getUserIdFromAccessToken())
            .orElseThrow(()->new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        // 권한 체크로 -> 로그인 완료되면 구현
        validProjectUser(project.getUser(), loginUser);

        // 프로젝트 기본 필드 수정
        project.setTitle(donationUpdateRequestDto.getTitle());
        project.setIntroduce(donationUpdateRequestDto.getIntroduce());

        // 프로젝트 상세 내용 업데이트
        project.setContent(donationUpdateRequestDto.getContent());

        // 이미지 업데이트
        List<ProjectImage> updatedImages = imageService.updateImageList
            (project.getProjectImage(), donationUpdateRequestDto.getContentImage(), project);
        project.setProjectImage(updatedImages);
        projectRepository.save(project);

        // Donation 관련 필드 업데이트
        donationService.updateDonation(project,donationUpdateRequestDto);
    }

    @Transactional
    public void deleteDonationProject(Long projectId) {
        //삭제 하려는 유저가 본인인지 확인하는 로직 필요
        User loginUser = userRepository.findById(tokenService.getUserIdFromAccessToken())
            .orElseThrow(()->new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
        Project project = findProjectById(projectId);
        validProjectUser(project.getUser(), loginUser);
        projectRepository.delete(project);
    }

    public ProjectResponseDto getProjectDetail(Long projectId) {
        Project project = findProjectById(projectId);

        if (project.getProjectType() == ProjectType.PURCHASE) {
            return purchaseService.createPurchaseProjectResponse(project);
        } else if (project.getProjectType() == ProjectType.DONATION) {
            return donationService.createDonationProjectResponse(project);
        } else {
            throw new BusinessLogicException(ExceptionCode.INVALID_PROJECT_TYPE);
        }
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
