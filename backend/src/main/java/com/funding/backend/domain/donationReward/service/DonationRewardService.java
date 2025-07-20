package com.funding.backend.domain.donationReward.service;

import com.funding.backend.domain.donation.dto.request.DonationRewardRequestDto;
import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.donation.repository.DonationRepository;
import com.funding.backend.domain.donationReward.dto.request.DonationRewardCreateRequestDto;
import com.funding.backend.domain.donationReward.dto.request.DonationRewardUpdateRequestDto;
import com.funding.backend.domain.donationReward.dto.response.DonationRewardResponseDto;
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
import java.util.Optional;
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
    public void createDonationRewardForProject(Long donationId, ProjectCreateRequestDto rewardRequestDto) {
        Donation donation = donationRepository.findById(donationId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DONATION_NOT_FOUND));
        List<DonationRewardRequestDto> donationRewardList = rewardRequestDto.getDonationDetail().getDonationRewardList();

        // 매핑된 DTO로 옵션 생성
        for (DonationRewardRequestDto dto : donationRewardList) {
            createRewardWithProject(donation, dto);
        }
    }


    @Transactional
    public void createDonationReward(Long projectId, DonationRewardCreateRequestDto requestDto) {
        User loginUser = userRepository.findById(tokenService.getUserIdFromAccessToken())
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        Donation donation = getVerifiedDonationByProjectId(projectId);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PROJECT_NOT_FOUND));
        validateProjectCreator(project, loginUser);

        createDonationReward(donation, requestDto);
    }


    @Transactional
    public void updateDonationReward(Long donationRewardId, DonationRewardUpdateRequestDto requestDto) {
        //수정하려는 사람이 해당 프로젝트를 생성한 사람인지 확인
        Donation donation = donationRepository.findByDonationRewardId(donationRewardId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DONATION_NOT_FOUND));
        User loginUser = userRepository.findById(tokenService.getUserIdFromAccessToken())
            .orElseThrow(()->new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        validateProjectCreator(donation.getProject(),loginUser);
        DonationReward donationReward = findDonationRewardById(donationRewardId);

        // 공통 필드 업데이트
        Optional.ofNullable(requestDto.getTitle())
            .ifPresent(donationReward::setTitle);
        Optional.ofNullable(requestDto.getContent())
            .ifPresent(donationReward::setContent);
        Optional.ofNullable(requestDto.getPrice())
            .ifPresent(donationReward::setPrice);

        donationRewardRepository.save(donationReward);
    }

    @Transactional
    public void deleteDonationReward(Long rewardId) {
        Donation donation = donationRepository.findByDonationRewardId(rewardId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DONATION_NOT_FOUND));
        User loginUser = userRepository.findById(tokenService.getUserIdFromAccessToken())
            .orElseThrow(()->new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        DonationReward donationReward = findDonationRewardById(rewardId);
        validateProjectCreator(donation.getProject(),loginUser);

        donationRewardRepository.delete(donationReward);
    }


    public List<DonationRewardResponseDto> getDonationRewardByProject(Long projectId) {
        Donation donation = getVerifiedDonationByProjectId(projectId);
        List<DonationReward> rewardList = donationRewardRepository.findAllByDonation(donation);

        return rewardList.stream()
            .map(DonationRewardResponseDto::new)
            .collect(Collectors.toList());
    }


    public DonationReward findDonationRewardById(Long donationRewardId) {
        return donationRewardRepository.findById(donationRewardId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DONATION_REWARD_NOT_FOUND));
    }

    //순환 참조 이슈로 따로 구현한 메서드
    private Donation getVerifiedDonationByProjectId(Long projectId) {
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


    private void createRewardWithProject(Donation donation, DonationRewardRequestDto requestDto) {
        DonationReward reward = DonationReward.builder()
            .price(requestDto.getPrice())
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .donation(donation)
            .build();
        donationRewardRepository.save(reward);

    }


    private void createDonationReward(Donation donation, DonationRewardCreateRequestDto requestDto) {
        DonationReward reward = DonationReward.builder()
            .price(requestDto.getPrice())
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .donation(donation)
            .build();
        donationRewardRepository.save(reward);
    }


}
