package com.funding.backend.domain.purchaseOption.controller;

import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/purchaseOption")
@Validated
@AllArgsConstructor
@Tag(name = "구매 옵션 관리  컨트롤러")
@Slf4j
public class OptionController {

    @PostMapping("/purchase/{purchaseId}/options")
    @Operation(
            summary = "구매형 옵션 등록",
            description = "구매형 프로젝트에 연결된 옵션(PurchaseOption)을 생성합니다. 옵션명, 설명, 가격 등의 정보를 포함합니다."
    )
    public ResponseEntity<?> createPurchaseOptions(
            @PathVariable Long purchaseId,
            @RequestBody @Valid List<PurchaseOptionRequestDto> optionDtos
    ) {
        purchaseOptionService.createPurchaseOptions(purchaseId, optionDtos);
        return new ResponseEntity<>(
                ApiResponse.of(HttpStatus.CREATED.value(), "구매형 옵션 생성 성공"),
                HttpStatus.CREATED
        );
    }

}
