package com.funding.backend.domain.donationReward.controller;

import com.funding.backend.domain.donationReward.dto.request.DonationRewardCreateRequestDto;
import com.funding.backend.domain.donationReward.dto.request.DonationRewardUpdateRequestDto;
import com.funding.backend.domain.donationReward.dto.response.DonationRewardResponseDto;
import com.funding.backend.domain.donationReward.service.DonationRewardService;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/donationReward")
@Validated
@AllArgsConstructor
@Tag(name = "후원 리워드 관리 컨트롤러")
@Slf4j
public class DonationRewardController {

    private final DonationRewardService donationRewardService;

    @PostMapping(value = "/{projectId}")
    @Operation(
        summary = "후원 리워드 생성",
        description = "후원 리워드(DonationReward)를 생성합니다."
    )
    public ResponseEntity<ApiResponse<Void>> createDonationReward(
        @PathVariable("projectId") Long projectId,
        @ModelAttribute @Valid DonationRewardCreateRequestDto requestDto
    ) {
      donationRewardService.createDonationReward(projectId, requestDto);
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(ApiResponse.of(HttpStatus.CREATED.value(), "후원 리워드 생성 성공"));
    }


    @PutMapping(value = "/{rewardId}")
    @Operation(
        summary = "후원 리워드 수정",
        description = "후원 리워드(DonationReward)를 수정합니다."
    )
    public ResponseEntity<ApiResponse<Void>> updateDonationReward(
        @PathVariable("rewardId") Long rewardId,
        @ModelAttribute @Valid DonationRewardUpdateRequestDto requestDto
    ) {
        donationRewardService.updateDonationReward(rewardId, requestDto);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.of(HttpStatus.OK.value(), "후원 리워드 수정 성공"));
    }

    @GetMapping("/{projectId}")
    @Operation(
        summary = "후원 리워드 조회",
        description = "특정 프로젝트에 등록된 모든 후원 리워드(DonationReward)를 조회합니다."
    )
    public ResponseEntity<ApiResponse<List<DonationRewardResponseDto>>> getDonationRewardByProject(
        @PathVariable("projectId") Long projectId
    ){
        List<DonationRewardResponseDto> rewards = donationRewardService.getDonationRewardByProject(projectId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.of(HttpStatus.OK.value(), "후원 리워드 조회 성공", rewards));
    }


}
