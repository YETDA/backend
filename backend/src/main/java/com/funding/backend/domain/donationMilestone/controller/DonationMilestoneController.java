package com.funding.backend.domain.donationMilestone.controller;

import com.funding.backend.domain.donationMilestone.service.DonationMilestoneService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/donationMilestone")
@Validated
@AllArgsConstructor
@Tag(name = "후원 마일스톤(로드랩) 관리 컨트롤러")
@Slf4j
public class DonationMilestoneController {

    private final DonationMilestoneService donationMilestoneService;

}
