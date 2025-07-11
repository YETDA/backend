package com.funding.backend.domain.donation.controller;

import com.funding.backend.domain.donation.dto.request.DonationUpdateRequestDto;
import com.funding.backend.domain.project.dto.request.DonationCreateRequestDto;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @Operation(summary = "후원형 프로젝트 생성", description = "후원형(Donation) 프로젝트를 생성합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createDonationProject(
        @RequestPart("requestDto") @Valid DonationCreateRequestDto requestDto,
        @RequestPart(value = "contentImage", required = false) List<MultipartFile> contentImages
    ) {
        log.info(requestDto.getContent());
        log.info(contentImages.get(0).getName());
        donationProjectService.createDonationProject(requestDto);
        requestDto.setContentImage(contentImages);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.of(HttpStatus.CREATED.value(), "후원형 프로젝트 생성 성공"));
    }


    @PutMapping(value = "/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "후원형 프로젝트 수정", description = "기존 후원형(Donation) 프로젝트를 수정합니다.")
    public ResponseEntity<?> updateDonationProject(
        @PathVariable("projectId") Long projectId,
        @RequestPart("requestDto") @Valid DonationUpdateRequestDto requestDto,
        @RequestPart(value = "contentImages", required = false) List<MultipartFile> contentImages
    ) {
        requestDto.setContentImage(contentImages);
        donationProjectService.updateDonationProject(projectId, requestDto);

        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "구매형 프로젝트 수정 성공"));
    }


    //프로젝트에서 삭제하는거 있긴 한데, 혹시 몰라서 만들어둠
    @DeleteMapping("/{projectId}")
    @Operation(
        summary = "후원형 프로젝트 삭제",
        description = "후원형(Donation) 프로젝트를 삭제합니다. 구매형/후원형에 관계없이 공통으로 삭제됩니다."
    )
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long projectId) {
        donationService.deleteDonation(projectId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.of(HttpStatus.OK.value(), "프로젝트 삭제 성공"));
    }


}
