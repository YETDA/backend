package com.funding.backend.domain.purchaseOption.controller;

import com.funding.backend.domain.purchaseOption.dto.request.PurchaseOptionCreateRequestDto;
import com.funding.backend.domain.purchaseOption.dto.request.PurchaseOptionUpdateRequestDto;
import com.funding.backend.domain.purchaseOption.service.PurchaseOptionService;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/purchaseOption")
@Validated
@AllArgsConstructor
@Tag(name = "구매 옵션 관리 컨트롤러")
@Slf4j
public class PurchaseOptionController {

    private final PurchaseOptionService purchaseOptionService;

    @PostMapping(value = "/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "구매 옵션 생성",
            description = "구매 옵션(PurchaseOption)을 생성합니다."
    )
    public ResponseEntity<ApiResponse<Void>> createPurchaseProject(
            @PathVariable Long projectId,
            @ModelAttribute @Valid PurchaseOptionCreateRequestDto requestDto
    ) {
        purchaseOptionService.createPurchaseProject(projectId,requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "구매 옵션 생성 성공"));
    }


    @PutMapping(value = "/{optionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "구매 옵션 수정",
            description = "구매 옵션(PurchaseOption)을 수정합니다. 파일, 가격, 상태, 설명 등을 변경할 수 있습니다."
    )
    public ResponseEntity<ApiResponse<Void>> updatePurchaseOption(
            @PathVariable Long optionId,
            @ModelAttribute @Valid PurchaseOptionUpdateRequestDto requestDto
    ) {
        purchaseOptionService.updatePurchaseOption(optionId, requestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "구매 옵션 수정 성공"));
    }



}
