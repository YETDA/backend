package com.funding.backend.domain.donationMilestone.controller;

import com.funding.backend.domain.donationMilestone.dto.request.DonationMilestoneCreateDto;
import com.funding.backend.domain.donationMilestone.service.DonationMilestoneService;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/donationMilestone")
@Validated
@AllArgsConstructor
@Tag(name = "후원 로드맵(마일스톤) 관리 컨트롤러")
@Slf4j
public class DonationMilestoneController {

    private final DonationMilestoneService donationMilestoneService;

    @PostMapping(value = "/{projectId}")
    @Operation(
        summary = "후원 로드맵(마일스톤) 생성",
        description = "후원 프로젝트 생성 시 로드맵(DonationMilestone)을 생성합니다."
    )
    public ResponseEntity<ApiResponse<Void>> createDonationMilestone(
        @PathVariable("projectId") Long projectId,
        @ModelAttribute @Valid DonationMilestoneCreateDto requestDto
    ) {
        donationMilestoneService.createDonationMilestone(projectId, requestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.of(HttpStatus.CREATED.value(), "후원 로드맵 생성 성공"));
    }

}
