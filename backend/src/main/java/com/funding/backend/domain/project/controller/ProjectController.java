package com.funding.backend.domain.project.controller;


import com.funding.backend.domain.project.dto.request.ProjectCreateRequestDto;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/project")
@Validated
@AllArgsConstructor
@Tag(name = "유저 관리 컨트롤러")
@Slf4j
public class ProjectController {

    private final ProjectService projectService;



    @PostMapping("/purchase")
    @Operation(
            summary = "구매형 프로젝트 생성",
            description = "구매형 프로젝트(Purchase)를 생성합니다. 제공 방식, Git 주소, 다운로드 제한 등의 정보를 포함합니다."
    )
    public ResponseEntity<?> createPurchaseProject(@RequestBody @Valid ProjectCreateRequestDto requestDto) {
        projectService.createPurchaseProject(requestDto);
        return new ResponseEntity<>(
                ApiResponse.of(HttpStatus.CREATED.value(), "구매형 프로젝트 생성 성공"),
                HttpStatus.CREATED
        );
    }



}
