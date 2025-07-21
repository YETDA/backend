package com.funding.backend.domain.donationMilestone.service;

import com.funding.backend.domain.donation.dto.request.DonationMilestoneRequestDto;
import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.donation.repository.DonationRepository;
import com.funding.backend.domain.donationMilestone.dto.request.DonationMilestoneCreateDto;
import com.funding.backend.domain.donationMilestone.dto.request.DonationMilestoneUpdateDto;
import com.funding.backend.domain.donationMilestone.dto.response.DonationMilestoneResponseDto;
import com.funding.backend.domain.donationMilestone.entity.DonationMilestone;
import com.funding.backend.domain.donationMilestone.repository.DonationMilestoneRepository;
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
public class DonationMilestoneService {

    private final DonationRepository donationRepository;
    private final ProjectRepository projectRepository;

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final DonationMilestoneRepository donationMilestoneRepository;


    @Transactional
    public void createDonationMilestoneByProject(Long donationId, ProjectCreateRequestDto milestoneRequestDto) {
        Donation donation = donationRepository.findById(donationId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DONATION_NOT_FOUND));

        List<DonationMilestoneRequestDto> donationMilestoneList = milestoneRequestDto.getDonationDetail().getDonationMilestoneList();

        //매핑된 DTO로 로드맵 생성
        for (DonationMilestoneRequestDto dto : donationMilestoneList) {
            createMilestoneWithProject(donation, dto);
        }
    }


    @Transactional
    public void createDonationMilestone(Long projectId, DonationMilestoneCreateDto requestDto) {
        User loginUser = userRepository.findById(tokenService.getUserIdFromAccessToken())
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        Donation donation = getVerifiedDonationByProjectId(projectId);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PROJECT_NOT_FOUND));
        validateProjectCreator(project, loginUser);

        createDonationMilestone(donation, requestDto);
    }


    @Transactional
    public void updateDonationMilestone(Long milestoneId, DonationMilestoneUpdateDto requestDto) {
        //수정하려는 사람이 해당 프로젝트를 생성한 사람인지 확인하는 로직
        Donation donation = donationRepository.findByDonationMilestoneId(milestoneId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DONATION_NOT_FOUND));
        User loginUser = userRepository.findById(tokenService.getUserIdFromAccessToken())
            .orElseThrow(()->new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        validateProjectCreator(donation.getProject(), loginUser);
        DonationMilestone donationMilestone = findDonationMilestoneById(milestoneId);

        //공통 필드 업데이트
        Optional.ofNullable(requestDto.getTitle())
            .ifPresent(donationMilestone::setTitle);
        Optional.ofNullable(requestDto.getContent())
            .ifPresent(donationMilestone::setContent);
        Optional.ofNullable(requestDto.getDueDate())
            .ifPresent(donationMilestone::setDueDate);

        donationMilestoneRepository.save(donationMilestone);
    }

    @Transactional
    public void deleteDonationMilestone(Long milestoneId) {
        Donation donation = donationRepository.findByDonationMilestoneId(milestoneId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DONATION_NOT_FOUND));
        User loginUser = userRepository.findById(tokenService.getUserIdFromAccessToken())
            .orElseThrow(()->new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        validateProjectCreator(donation.getProject(),loginUser);
        DonationMilestone donationMilestone = findDonationMilestoneById(milestoneId);

        donationMilestoneRepository.delete(donationMilestone);
    }


    public List<DonationMilestoneResponseDto> getDonationMilestoneByProject(Long projectId) {
        Donation donation = getVerifiedDonationByProjectId(projectId);
        List<DonationMilestone> milestoneList = donationMilestoneRepository.findAllByDonation(donation);

        return milestoneList.stream()
            .map(DonationMilestoneResponseDto::new)
            .collect(Collectors.toList());
    }


    public DonationMilestone findDonationMilestoneById(Long milestoneId) {
        return donationMilestoneRepository.findById(milestoneId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DONATION_MILESTONE_NOT_FOUND));
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


    private void createMilestoneWithProject(Donation donation, DonationMilestoneRequestDto milestoneRequestDto) {
        DonationMilestone milestone = DonationMilestone.builder()
            .title(milestoneRequestDto.getTitle())
            .content(milestoneRequestDto.getContent())
            .dueDate(milestoneRequestDto.getDueDate())
            .donation(donation)
            .build();
        donationMilestoneRepository.save(milestone);
    }

    private void createDonationMilestone(Donation donation, DonationMilestoneCreateDto requestDto) {
        DonationMilestone milestone = DonationMilestone.builder()
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .dueDate(requestDto.getDueDate())
            .donation(donation)
            .build();
        donationMilestoneRepository.save(milestone);
    }


}
