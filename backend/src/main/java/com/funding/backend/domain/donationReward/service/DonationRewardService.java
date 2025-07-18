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
import com.funding.backend.domain.purchase.dto.request.PurchaseOptionRequestDto;
import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.enums.ProvidingMethod;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.utils.s3.ImageService;
import com.funding.backend.global.utils.s3.S3FileInfo;
import com.funding.backend.security.jwt.TokenService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DonationRewardService {

    private final DonationRewardRepository donationRewardRepository;
    private final DonationRepository donationRepository;
    private final ProjectRepository projectRepository;

    private final ImageService imageService;
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

        Donation donation = getVerifiedPurchaseByProjectId(projectId);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PROJECT_NOT_FOUND));
        validateProjectCreator(project, loginUser);

        createDonationReward(donation, requestDto);
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
