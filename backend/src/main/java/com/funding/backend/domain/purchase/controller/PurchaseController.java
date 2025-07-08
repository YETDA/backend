package com.funding.backend.domain.purchase.controller;

import com.funding.backend.domain.project.dto.request.ProjectCreateRequestDto;
import com.funding.backend.domain.project.dto.response.PurchaseProjectResponseDto;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.purchase.dto.request.PurchaseUpdateRequestDto;
import com.funding.backend.domain.purchase.service.PurchaseService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<?> createPurchaseProject(
            @RequestPart("requestDto") @Valid ProjectCreateRequestDto requestDto,
            @RequestPart(value = "contentImage", required = false) List<MultipartFile> contentImages,
            @RequestPart(value = "optionFiles", required = false) List<MultipartFile> optionFiles
    ) {
        // 프로젝트 이미지 저장
        requestDto.setContentImage(contentImages);


        //프로젝트 구매 옵션 파일 저장 후 url 매핑
        purchaseService.matchOptionFilesToDto(requestDto.getPurchaseDetail().getPurchaseOptionList(), optionFiles);


        projectService.createPurchaseProject(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "구매형 프로젝트 생성 성공"));
    }


    @PutMapping(value = "/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "구매형 프로젝트 수정", description = "기존 구매형 프로젝트(Purchase)를 수정합니다.")
    public ResponseEntity<?> updatePurchaseProject(
            @PathVariable Long projectId,
            @RequestPart("requestDto") @Valid PurchaseUpdateRequestDto requestDto,
            @RequestPart(value = "contentImages", required = false) List<MultipartFile> contentImages
    ) {
        requestDto.setContentImage(contentImages);
        projectService.updatePurchaseProject(projectId, requestDto);

        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "구매형 프로젝트 수정 성공"));
    }



    @GetMapping("/{projectId}")
    @Operation(
            summary = "구매형 프로젝트 상세 조회",
            description = "구매형 프로젝트(Purchase)의 상세 정보를 조회합니다. 제목, 소개, Git 주소, 제공 방식, 가격제도, 파일 정보 등을 반환합니다."
    )
    public ResponseEntity<PurchaseProjectResponseDto> getPurchaseProject(
            @PathVariable Long projectId
    ) {
        PurchaseProjectResponseDto response = projectService.getPurchaseProject(projectId);
        return ResponseEntity.ok(response);
    }


    //프로젝트에서 삭제하는거 있긴한데, 혹시 몰라서 만들어둠
    @DeleteMapping("/{projectId}")
    @Operation(
            summary = "구매형 프로젝트 삭제",
            description = "구매형 프로젝트(Purchase)를 삭제합니다. 해당 프로젝트의 생성자만 삭제할 수 있습니다."
    )
    public ResponseEntity<?> deletePurchaseProject(
            @PathVariable Long projectId
    ) {
        purchaseService.deletePurchase(projectId);
        return new ResponseEntity<>(
                ApiResponse.of(HttpStatus.OK.value(), "구매형 프로젝트 삭제 성공"),
                HttpStatus.OK
        );
    }



}
