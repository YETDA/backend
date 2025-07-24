package com.funding.backend.domain.donation.service;

import com.funding.backend.domain.donation.dto.request.DonationUpdateRequestDto;
import com.funding.backend.domain.donation.dto.response.DonationInfoResponseDto;
import com.funding.backend.domain.donation.dto.response.DonationListResponseDto;
import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.donation.repository.DonationRepository;
import com.funding.backend.domain.donation.dto.request.DonationProjectDetail;
import com.funding.backend.domain.follow.service.FollowService;
import com.funding.backend.domain.mainCategory.entity.MainCategory;
import com.funding.backend.domain.mainCategory.service.MainCategoryService;
import com.funding.backend.domain.order.service.OrderService;
import com.funding.backend.domain.project.dto.response.DonationProjectResponseDto;
import com.funding.backend.domain.project.dto.response.ProjectResponseDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.project.service.ProjectViewCountService;
import com.funding.backend.domain.projectSubCategory.entity.ProjectSubCategory;
import com.funding.backend.domain.projectSubCategory.service.ProjectSubCategoryService;
import com.funding.backend.domain.subjectCategory.entity.SubjectCategory;
import com.funding.backend.domain.subjectCategory.service.SubjectCategoryService;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.TokenService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DonationService {

    private final ProjectRepository projectRepository;
    private final DonationRepository donationRepository;

    private final MainCategoryService mainCategoryService;
    private final SubjectCategoryService subjectCategoryService;
    private final ProjectSubCategoryService projectSubCategoryService;
    private final UserService userService;
    private final TokenService tokenService;
    private final FollowService followService;
    private final OrderService orderService;
    private final ProjectViewCountService projectViewCountService;

    @Transactional
    public Donation createDonation(Project project, DonationProjectDetail dto) {

        MainCategory mainCategory = mainCategoryService.findDonationCategoryById(dto.getMainCategoryId());

        List<Long> subCategoryIds = dto.getSubCategoryIds();
        List<SubjectCategory> subjectCategories = new ArrayList<>();

        if (subCategoryIds != null && !subCategoryIds.isEmpty()) {
          subjectCategories = subjectCategoryService.findCategoriesByIds(subCategoryIds);
        }

        Donation donation = Donation.builder()
            .project(project)
            .mainCategory(mainCategory)
            .startDate(dto.getStartDate().atStartOfDay())
            .endDate(dto.getEndDate().atStartOfDay())
            .gitAddress(dto.getGitAddress())
            .deployAddress(dto.getDeployAddress())
            .appStoreAddress(dto.getAppStoreAddress())
            .build();

        Donation savedDonation = donationRepository.save(donation);

        if (!subjectCategories.isEmpty()) {
          List<ProjectSubCategory> projectSubCategories = projectSubCategoryService.createProjectSubCategories(subjectCategories, savedDonation);
          savedDonation.setProjectSubCategories(projectSubCategories);
        }

        return savedDonation;
    }

    @Transactional
    public void updateDonation(Project project, DonationUpdateRequestDto dto) {
        Donation donation = findByProject(project);

        if (dto.getMainCategoryId() != null) {
          MainCategory mainCategory = mainCategoryService.findDonationCategoryById(dto.getMainCategoryId());
          donation.setMainCategory(mainCategory);
        }
        Optional.ofNullable(dto.getGitAddress())
            .ifPresent(donation::setGitAddress);
        Optional.ofNullable(dto.getAppStoreAddress())
            .ifPresent(donation::setAppStoreAddress);

        donationRepository.save(donation);
    }

    @Transactional
    public void deleteDonation(Long projectId){
        Project project = projectRepository.findById(projectId)
            .orElseThrow(()->new BusinessLogicException(ExceptionCode.PROJECT_NOT_FOUND));

        Donation donation = donationRepository.findById(findByProject(project).getId())
            .orElseThrow(()->new BusinessLogicException(ExceptionCode.DONATION_NOT_FOUND));

        donationRepository.delete(donation);
        projectRepository.delete(project);
    }

    public Donation findByProject(Project project){
        return donationRepository.findByProject(project)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DONATION_NOT_FOUND));
    }


    public DonationProjectResponseDto createDonationProjectResponse(Project project) {
        Donation detail = findByProject(project);

        User user = userService.findUserById(tokenService.getUserIdFromAccessToken());
        Long projectCount = projectRepository.countByUserIdAndProjectStatusIn(user.getId(), Arrays.asList(
            ProjectStatus.RECRUITING, ProjectStatus.COMPLETED));
        Long followerCount = followService.countFollowers(user.getId());
        Long viewCount = projectViewCountService.viewCountProject(project.getId());

        return new DonationProjectResponseDto(
            project, detail, projectCount, followerCount, viewCount
        );
    }


    public Page<DonationListResponseDto> getMyDonationProjectList(Pageable pageable){
        User loginUser = userService.findUserById(tokenService.getUserIdFromAccessToken());
        Page<Project> donationProjectList = projectRepository.findByUserIdAndProjectTypeWithDonation(
            loginUser.getId(), ProjectType.DONATION, pageable);

        return donationProjectList.map(project -> {
          //후원 정산 구현 완료되면 수정할 예정(지금은 임시로)
          Long sellCount = orderService.donationOrderCount(project);
          return new DonationListResponseDto(project, sellCount);
        });
    }

    public Page<DonationInfoResponseDto> getDonationMainCategoryList(
        Long categoryId, Pageable pageable, ProjectStatus projectStatus) {

        MainCategory mainCategory = mainCategoryService.findDonationCategoryById(categoryId);

        Page<Project> projectPage = projectRepository.findByDonationCategoryAndStatus(
            mainCategory.getId(), projectStatus, pageable);

        // 프로젝트 ID 리스트 추출
        List<Long> projectIds = projectPage.stream()
            .map(Project::getId)
            .toList();

        // ID별 판매 수량 Map 조회
        List<Object[]> orderCounts = orderService.countOrdersByProjectIds(projectIds);

        Map<Long, Long> projectIdToSellCount = orderCounts.stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> (Long) row[1]
            ));

        // DTO 변환
        //Long sellCount = projectIdToSellCount.getOrDefault(project.getId(), 0L);
        return projectPage.map(DonationInfoResponseDto::new);
      }

}
