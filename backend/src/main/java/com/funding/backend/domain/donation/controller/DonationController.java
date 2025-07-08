package com.funding.backend.domain.donation.controller;

import com.funding.backend.domain.donation.dto.request.DonationCreateRequestDto;
import com.funding.backend.domain.donation.service.DonationService;
import com.funding.backend.domain.donation.service.DonationProjectService;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/project/donation")
@Validated
@AllArgsConstructor
@Tag(name = "프로젝트 후원 관리 컨트롤러")
@Slf4j
public class DonationController {

    private final DonationService donationService;
    private final DonationProjectService donationProjectService;

    @PostMapping(consumes = {"multipart/form"})
    @Operation(summary = "후원형 프로젝트 생성", description = "후원형(Donation) 프로젝트를 생성")
    public ResponseEntity<?> createDonationProject(
        @RequestPart("requestDto") @Valid DonationCreateRequestDto requestDto,
        @RequestPart(value = "contentImage", required = false) List<MultipartFile> contentImages
    ) {
        // 프로젝트 이미지 저장
        requestDto.setContentImage(contentImages);

        donationProjectService.createDonationProject(requestDto);


        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.of(HttpStatus.CREATED.value(), "후원형 프로젝트 생성 성공"));
    }

}
