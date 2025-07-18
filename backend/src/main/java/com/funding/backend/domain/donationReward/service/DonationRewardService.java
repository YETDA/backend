package com.funding.backend.domain.donationReward.service;

import com.funding.backend.domain.donation.dto.request.DonationRewardRequestDto;
import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.donation.repository.DonationRepository;
import com.funding.backend.domain.donationReward.dto.request.DonationRewardCreateRequestDto;
import com.funding.backend.domain.donationReward.entity.DonationReward;
import com.funding.backend.domain.donationReward.repository.DonationRewardRepository;
import com.funding.backend.domain.project.dto.request.ProjectCreateRequestDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.TokenService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DonationRewardService {

    private final DonationRewardRepository donationRewardRepository;
    private final DonationRepository donationRepository;
    private final ProjectRepository projectRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;


    @Transactional
    public void createDonationReward(Long projectId, List<DonationRewardCreateRequestDto> rewardDtos) {
        User loginUser = userRepository.findById(tokenService.getUserIdFromAccessToken())
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PROJECT_NOT_FOUND));
        validateProjectCreator(project, loginUser);

        Donation donation = getVerifiedPurchaseByProjectId(projectId);

        List<DonationReward> rewards = rewardDtos.stream()
            .map(dto -> DonationReward.builder()
                .price(dto.getPrice())
                .title(dto.getTitle())
                .content(dto.getContent())
                .donation(donation)
                .build())
            .collect(Collectors.toList());

        donationRewardRepository.saveAll(rewards);
    }



    //순환 참조 이슈로 따로 구현한 메서드
    private Donation getVerifiedPurchaseByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PROJECT_NOT_FOUND));

        return donationRepository.findByProject(project)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DONATION_NOT_FOUND));

    }

    private void validateProjectCreator(Project project, User user) {
        if (!project.getUser().equals(user)) {
            throw new BusinessLogicException(ExceptionCode.NOT_PROJECT_CREATOR);
        }
    }


}
