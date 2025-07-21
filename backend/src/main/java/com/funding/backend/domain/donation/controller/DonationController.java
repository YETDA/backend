package com.funding.backend.domain.donation.controller;

import com.funding.backend.domain.donation.dto.request.DonationUpdateRequestDto;
import com.funding.backend.domain.donation.dto.response.DonationListResponseDto;
import com.funding.backend.domain.donation.dto.response.DonationResponseDto;
import com.funding.backend.domain.donation.service.DonationService;
import com.funding.backend.domain.donation.service.DonationProjectService;
import com.funding.backend.domain.project.dto.request.ProjectCreateRequestDto;
import com.funding.backend.domain.project.dto.response.ProjectResponseDto;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@Tag(name = "후원 프로젝트 관리 컨트롤러")
@Slf4j
public class DonationController {

    private final DonationService donationService;
    private final DonationProjectService donationProjectService;
    private final ProjectService projectService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "후원형 프로젝트 생성", description = "후원형(Donation) 프로젝트를 생성합니다.")
    public ResponseEntity<ApiResponse<DonationResponseDto>> createDonationProject(
        @RequestPart("requestDto") @Valid ProjectCreateRequestDto requestDto,
        @RequestPart(value = "contentImage", required = false) List<MultipartFile> contentImages
    ) {
        requestDto.setContentImage(contentImages);
        DonationResponseDto response = donationProjectService.createDonationProject(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.of(HttpStatus.CREATED.value(), "후원형 프로젝트 생성 성공", response));
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
    public ResponseEntity<ApiResponse<Void>> deleteDonation(@PathVariable Long projectId) {
        donationService.deleteDonation(projectId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.of(HttpStatus.OK.value(), "프로젝트 삭제 성공"));
    }

    @GetMapping("/{projectId}")
    @Operation(
        summary = "프로젝트 상세 조회",
        description = "구매용, 후원용에 따라 응답 형식이 달라집니다."
    )
    public ResponseEntity<ApiResponse<ProjectResponseDto>> getProjectDetail(@PathVariable Long projectId) {
        ProjectResponseDto response = projectService.getProjectDetail(projectId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.of(HttpStatus.OK.value(), "프로젝트 상세 조회 성공", response));
    }


    @GetMapping("/me/list")
    @Operation(
        summary = "내가 생성한 후원형 프로젝트 목록 조회",
        description = "현재 로그인한 사용자가 생성한 모든 후원형 프로젝트(Donation)를 최신순으로 조회합니다."
    )
    public ResponseEntity<ApiResponse<Page<DonationListResponseDto>>> getMyDonationProjects(@ParameterObject Pageable pageable) {
        Page<DonationListResponseDto> projects = donationService.getMyDonationProjectList(pageable);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.of(HttpStatus.OK.value(), "내가 생성한 후원형 프로젝트 목록 조회 성공", projects));
    }


}
