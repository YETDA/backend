package com.funding.backend.domain.donation.service;

import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.donation.repository.DonationRepository;
import com.funding.backend.domain.donation.dto.request.DonationProjectDetail;
import com.funding.backend.domain.project.entity.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DonationService {

  private final DonationRepository donationRepository;

  @Transactional
  public void createDonation(Project project, DonationProjectDetail dto){
    Donation donation = Donation.builder()
        .project(project)
        .priceGoal(dto.getGoalAmount())
        .startDate(dto.getStartDate().atStartOfDay())
        .endDate(dto.getEndDate().atStartOfDay())
        .gitAddress(dto.getGitAddress())
        .deployAddress(dto.getDeployAddress())
        .build();
    donationRepository.save(donation);
  }
}
