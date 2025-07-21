package com.funding.backend.domain.donationMilestone.service;

import com.funding.backend.domain.donation.repository.DonationRepository;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.security.jwt.TokenService;
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

}
