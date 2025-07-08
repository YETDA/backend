package com.funding.backend.domain.purchaseOption.controller;

import com.funding.backend.domain.purchaseOption.dto.request.PurchaseOptionRequestDto;
import com.funding.backend.domain.purchaseOption.service.PurchaseOptionService;
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
import org.springframework.web.bind.annotation.PathVariable;
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

    @PutMapping("/{purchaseId}")
    @Operation(
            summary = "구매형 옵션 수정",
            description = "기존 구매형 프로젝트 옵션(PurchaseOption)을 전체 교체합니다. 기존 옵션은 모두 삭제되고, 전달된 새 옵션 리스트로 대체됩니다."
    )
    public ResponseEntity<?> updatePurchaseOptions(
            @PathVariable Long purchaseId,
            @RequestBody @Valid List<PurchaseOptionRequestDto> purchaseOptionRequestDto
    ) {
        purchaseOptionService.updateOptions(purchaseId, purchaseOptionRequestDto);
        return new ResponseEntity<>(
                ApiResponse.of(HttpStatus.OK.value(), "구매형 옵션 수정 성공"),
                HttpStatus.OK
        );
    }

}
