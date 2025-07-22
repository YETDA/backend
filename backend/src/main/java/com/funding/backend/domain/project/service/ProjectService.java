package com.funding.backend.domain.project.service;

import com.funding.backend.domain.alarm.event.context.NewPurchaseProjectContext;
import com.funding.backend.domain.donation.service.DonationProjectService;
import com.funding.backend.domain.donation.service.DonationService;
import com.funding.backend.domain.pricingPlan.repository.PricingRepository;
import com.funding.backend.domain.pricingPlan.service.PricingService;
import com.funding.backend.domain.project.dto.request.ProjectCreateRequestDto;
import com.funding.backend.domain.project.dto.response.ProjectCountResponseDto;
import com.funding.backend.domain.project.dto.response.ProjectResponseDto;
import com.funding.backend.domain.project.dto.response.AuditProjectResponseDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.dto.response.ProjectInfoResponseDto;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.domain.purchase.dto.request.PurchaseUpdateRequestDto;
import com.funding.backend.domain.purchase.dto.response.PurchaseResponseDto;
import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.purchase.service.PurchaseService;
import com.funding.backend.domain.purchaseOption.service.PurchaseOptionService;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.*;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.utils.s3.ImageService;
import com.funding.backend.security.jwt.TokenService;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final DonationService donationService;
    private final PricingRepository pricingRepository;

    private final PricingService pricingService;
    private final ImageService imageService;
    private final PurchaseService purchaseService;
    private final UserRepository userRepository;
    private final PurchaseOptionService purchaseOptionService;
    private final DonationProjectService donationProjectService;
    private final ApplicationEventPublisher eventPublisher;

    private final TokenService tokenService;
    private final UserService userService;

    @Transactional
    public PurchaseResponseDto createPurchaseProject(ProjectCreateRequestDto dto) {
        User loginUser = userRepository.findById(tokenService.getUserIdFromAccessToken())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
        validateBankAccountPresence(loginUser);
        Project project = Project.builder()
                .introduce(dto.getIntroduce())
                .title(dto.getTitle())
                .content(dto.getContent())
                .projectStatus(ProjectStatus.UNDER_AUDIT) //처음 만들때는 심사중으로
                .pricingPlan(pricingService.findById(dto.getPricingPlanId()))
                .projectType(ProjectType.PURCHASE)
                .user(loginUser)
                .build();

        //여기서도 이미지가 존재하는 경우만 저장되게
        Project saveProject = projectRepository.save(project);

        List<ProjectImage> projectImage = new ArrayList<>();
        if (dto.getContentImage() != null && !dto.getContentImage().isEmpty()) {
            projectImage = imageService.saveImageList(dto.getContentImage(), project);
        }
        project.setProjectImage(projectImage);


        //구매 프로젝트 정보 저장
        Purchase createPurchase = purchaseService.createPurchase(saveProject, dto.getPurchaseDetail());
        // 옵션 저장
        if (dto.getPurchaseDetail().getPurchaseOptionList() != null && !dto.getPurchaseDetail().getPurchaseOptionList().isEmpty()) {
            purchaseOptionService.createPurchaseOptionForProject(createPurchase.getId(), dto);
        }

        //알림 생성
        eventPublisher.publishEvent(new NewPurchaseProjectContext(loginUser.getId(), project.getTitle()
                ,project.getProjectStatus(),project.getProjectType(),project.getPricingPlan()));

        return new PurchaseResponseDto(saveProject.getId());
    }


    @Transactional
    public void updatePurchaseProject(Long projectId, PurchaseUpdateRequestDto purchaseUpdateRequestDto) {
        Project project = findProjectById(projectId);
        User loginUser = userRepository.findById(tokenService.getUserIdFromAccessToken())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        // 권한 체크로 -> 로그인 완료되면 구현
        validProjectUser(project.getUser(), loginUser);

        // 프로젝트 기본 필드 수정
        project.setTitle(purchaseUpdateRequestDto.getTitle());
        project.setIntroduce(purchaseUpdateRequestDto.getIntroduce());

        // 프로젝트 상세 내용 업데이트
        project.setContent(purchaseUpdateRequestDto.getContent());
        if(purchaseUpdateRequestDto.getContentImage() != null && !purchaseUpdateRequestDto.getContentImage().isEmpty()){
            // 이미지 업데이트
            List<ProjectImage> updatedImages = imageService.updateImageList
                    (project.getProjectImage(), purchaseUpdateRequestDto.getContentImage(), project);
            project.setProjectImage(updatedImages);
        }
        projectRepository.save(project);
        // Purchase 관련 필드 업데이트
        purchaseService.updatePurchase(project, purchaseUpdateRequestDto);
    }


    public Project findProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.PROJECT_NOT_FOUND)
        );
    }

    public void validProjectUser(User projectUser, User loginUser) {
        if (!projectUser.equals(loginUser)) {
            throw new BusinessLogicException(ExceptionCode.NOT_PROJECT_CREATOR);
        }
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


    @Transactional
    public void deleteProject(Long projectId) {
        //삭제 하려는 유저가 본인인지 확인하는 로직 필요
        User loginUser = userRepository.findById(tokenService.getUserIdFromAccessToken())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
        Project project = findProjectById(projectId);
        validProjectUser(project.getUser(), loginUser);
        projectRepository.delete(project);
    }

    public Page<ProjectInfoResponseDto> getPopularProjects(ProjectTypeFilter requestProjectType, PopularProjectSortType requestSortType, Pageable pageable) {
        Page<Project> projects;

        if (requestSortType == PopularProjectSortType.LIKE) {
            if (requestProjectType == ProjectTypeFilter.ALL) {
                projects = projectRepository.findAllByOrderByLikesDesc(pageable);
            } else {
                ProjectType projectType = ProjectType.valueOf(requestProjectType.name());
                projects = projectRepository.findByProjectTypeOrderByLikesDesc(projectType, pageable);
            }
        } else if (requestSortType == PopularProjectSortType.SELLING_AMOUNT) {
            if (requestProjectType == ProjectTypeFilter.ALL) {
                projects = projectRepository.findAllByOrderBySellingAmountDesc(pageable);
            } else {
                ProjectType projectType = ProjectType.valueOf(requestProjectType.name());
                projects = projectRepository.findByProjectTypeOrderBySellingAmountDesc(projectType, pageable);
            }
        } else if (requestSortType == PopularProjectSortType.ACHIEVEMENT_RATE) {
            if (requestProjectType == ProjectTypeFilter.DONATION) {
                projects = projectRepository.findAllByOrderByAchievementRateDesc(pageable);
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_PROJECT_SEARCH_TYPE);
            }
        }
        else {
            // 다른 정렬 타입이 추가될 경우를 대비한 확장 지점
            throw new BusinessLogicException(ExceptionCode.INVALID_PROJECT_SEARCH_TYPE);
        }

        return projects.map(ProjectInfoResponseDto::new);
    }

    public Page<AuditProjectResponseDto> findProjectsByTypeAndStatus(ProjectType type, List<ProjectStatus> statuses, Pageable pageable) {
        return projectRepository.findByTypeAndStatuses(type, statuses, pageable).map(AuditProjectResponseDto::new);
    }

    @Transactional
    public AuditProjectResponseDto updateProjectStatus(Long projectId, ProjectStatus status) {
        Project project = findProjectById(projectId);
        project.setProjectStatus(status);

        return new AuditProjectResponseDto(project);
    }

    @Transactional
    public void updateAllProjectStatus(ProjectStatus oldStatus, ProjectStatus newStatus) {
        projectRepository.updateProjectStatusByStatus(oldStatus, newStatus);
    }

    private void validateBankAccountPresence(User user) {
        if (user.getAccount() == null && user.getBank() == null) {
            throw new BusinessLogicException(ExceptionCode.ACCOUNT_NOT_FOUND);
        }

        if (user.getAccount() == null || user.getBank() == null) {
            throw new BusinessLogicException(ExceptionCode.BANK_NOT_FOUND);
        }
    }

    //검색 기능
    @Transactional(readOnly = true)
    public Page<ProjectInfoResponseDto> searchProjectsByTitle(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty() || keyword.trim().length() < 2) {
            throw new BusinessLogicException(ExceptionCode.INVALID_SEARCH_KEYWORD);
        }
        return projectRepository.findByTitleContaining(keyword.trim(), pageable)
                .map(ProjectInfoResponseDto::new);
    }

    /**
     * 특정 사용자의 프로젝트 중 RECRUITING 또는 COMPLETED 상태인 프로젝트 조회
     */
    public Page<Project> getProjectsByUserAndStatus(Long userId, List<ProjectStatus> statuses, Pageable pageable) {
        return projectRepository.findByUserIdAndProjectStatusIn(userId, statuses, pageable);
    }

    public long getCountOfProjectsByUserAndStatus(Long userId, List<ProjectStatus> statuses) {
        return projectRepository.countByUserIdAndProjectStatusIn(userId, statuses);
    }

    public ProjectCountResponseDto countProject(){
       User user =  userService.findUserById(tokenService.getUserIdFromAccessToken());
       Long allCount = projectRepository.countByUserIdAndProjectStatusIn(user.getId(),
               Arrays.asList(ProjectStatus.RECRUITING, ProjectStatus.COMPLETED));

       Long projectCount =projectRepository.countByUserIdAndProjectTypeAndProjectStatusIn(user.getId(),
               ProjectType.PURCHASE,Arrays.asList(ProjectStatus.RECRUITING, ProjectStatus.COMPLETED));

       Long donationCount =projectRepository.countByUserIdAndProjectTypeAndProjectStatusIn(user.getId(),
                ProjectType.DONATION,Arrays.asList(ProjectStatus.RECRUITING, ProjectStatus.COMPLETED));
        return new ProjectCountResponseDto(allCount,donationCount,projectCount);

    }


}

