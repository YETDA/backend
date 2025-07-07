package com.funding.backend.domain.donation.controller;

import com.funding.backend.domain.project.dto.request.ProjectCreateRequestDto;
import com.funding.backend.domain.donation.service.DonationProjectService;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/project")
@Validated
@AllArgsConstructor
@Tag(name = "프로젝트 후원 관리 컨트롤러")
@Slf4j
public class DonationController {

    private final DonationProjectService DonationprojectService;


    @PostMapping("/donation")
    @Operation(
        summary = "후원형 프로젝트 생성",
        description = "후원형 프로젝트(Donation)를 생성합니다."
    )
    public ResponseEntity<?> createDonationProject(
        @RequestBody @Valid ProjectCreateRequestDto requestDto) {
        DonationprojectService.createDonationProject(requestDto);
        return new ResponseEntity<>(
            ApiResponse.of(HttpStatus.CREATED.value(), "후원형 프로젝트 생성 성공"),
            HttpStatus.CREATED
        );
    }

}
