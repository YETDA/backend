package com.funding.backend.domain.purchase.controller;

import com.funding.backend.domain.project.dto.request.ProjectCreateRequestDto;
import com.funding.backend.domain.project.dto.response.ProjectInfoResponseDto;
import com.funding.backend.domain.project.dto.response.PurchaseProjectResponseDto;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.purchase.dto.request.PurchaseUpdateRequestDto;
import com.funding.backend.domain.purchase.dto.response.PurchaseInfoResponseDto;
import com.funding.backend.domain.purchase.dto.response.PurchaseListResponseDto;
import com.funding.backend.domain.purchase.dto.response.PurchaseResponseDto;
import com.funding.backend.domain.purchase.service.PurchaseService;
import com.funding.backend.domain.purchaseOption.dto.response.PurchaseOptionResponseDto;
import com.funding.backend.enums.PopularProjectSortType;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectTypeFilter;
import com.funding.backend.enums.ProvidingMethod;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/project/purchase")
@Validated
@AllArgsConstructor
@Tag(name = "프로젝트 구매 관리 컨트롤러")
@Slf4j
public class PurchaseController {
    private final ProjectService projectService;
    private final PurchaseService purchaseService;


    @PostMapping(consumes = {"multipart/form-data"})
    @Operation(summary = "구매형 프로젝트 생성", description = "구매형 프로젝트 생성")
    public ResponseEntity<ApiResponse<PurchaseResponseDto>> createPurchaseProject(
            @RequestPart("requestDto") @Valid ProjectCreateRequestDto requestDto,
            @RequestPart(value = "contentImage", required = false) List<MultipartFile> contentImages,
            @RequestPart(value = "optionFiles", required = false) List<MultipartFile> optionFiles
    ) {
        requestDto.setContentImage(contentImages);
        requestDto.setOptionFiles(optionFiles);
        PurchaseResponseDto response  = projectService.createPurchaseProject(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(),"구매형 프로젝트 생성 성공",response));
    }


    @PutMapping(value = "/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "구매형 프로젝트 수정", description = "기존 구매형 프로젝트(Purchase)를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updatePurchaseProject(
            @PathVariable Long projectId,
            @RequestPart("requestDto") @Valid PurchaseUpdateRequestDto requestDto,
            @RequestPart(value = "contentImages", required = false) List<MultipartFile> contentImages
    ) {
        requestDto.setContentImage(contentImages);
        projectService.updatePurchaseProject(projectId, requestDto);

        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "구매형 프로젝트 수정 성공"));
    }

    //프로젝트에서 삭제하는거 있긴한데, 혹시 몰라서 만들어둠
    @DeleteMapping("/{projectId}")
    @Operation(
            summary = "구매형 프로젝트 삭제",
            description = "구매형 프로젝트(Purchase)를 삭제합니다. 해당 프로젝트의 생성자만 삭제할 수 있습니다."
    )
    public ResponseEntity<ApiResponse<Void>> deletePurchaseProject(
            @PathVariable Long projectId
    ) {
        purchaseService.deletePurchase(projectId);
        return new ResponseEntity<>(
                ApiResponse.of(HttpStatus.OK.value(), "구매형 프로젝트 삭제 성공"),
                HttpStatus.OK
        );
    }

    @GetMapping("/me/list")
    @Operation(
            summary = "내가 생성한 구매형 프로젝트 목록 조회",
            description = "현재 로그인한 사용자가 생성한 모든 구매형 프로젝트(Purchase)를 최신순으로 조회합니다."
    )
    public ResponseEntity<ApiResponse<Page<PurchaseListResponseDto>>> getMyPurchaseProjects(@ParameterObject Pageable pageable) {
        Page<PurchaseListResponseDto> projects = purchaseService.getMyPurchaseProjectList(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "내가 생성한 구매형 프로젝트 목록 조회 성공", projects));
    }



    @GetMapping("/category/{categoryId}")
    @Operation(
            summary = "카테고리별 구매 프로젝트 리스트 조회",
            description = "카테고리별 구매 프로젝트 리스트 조회"
    )
    public ResponseEntity<ApiResponse<Page<PurchaseInfoResponseDto>>> getCategoryList(
            @Parameter(description = "카테고리 ID") @PathVariable Long categoryId,
            @RequestParam ProjectStatus projectStatus,
            @ParameterObject Pageable pageable
    ) {
        Page<PurchaseInfoResponseDto> response = purchaseService.getPurchaseCategoryProjectList(categoryId, pageable, projectStatus);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "카테고리별 프로젝트 조회 성공", response));
    }

}
