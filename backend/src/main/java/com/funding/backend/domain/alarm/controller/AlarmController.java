package com.funding.backend.domain.alarm.controller;




import com.funding.backend.domain.alarm.service.AlarmService;
import com.funding.backend.domain.project.dto.response.ProjectInfoResponseDto;
import com.funding.backend.enums.PopularProjectSortType;
import com.funding.backend.enums.ProjectTypeFilter;
import com.funding.backend.global.utils.ApiResponse;
import com.funding.backend.security.jwt.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/alarm")
@Tag(name = "알림 컨트롤러")
@RequiredArgsConstructor
@Slf4j
public class AlarmController {
    private final AlarmService alarmService;
    private final TokenService tokenService;

    // SSE를 통한 실시간 알림 전송
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
            summary = "SSE 연결",
            description = "SSE 연결합니다."
    )
    public SseEmitter streamNotifications() throws IOException {
        return alarmService.createSseConnection();
    }


    @GetMapping("/user")
    @Operation(
            summary = "사용자의 모든 알림 조회",
            description = ""
    )
    public ResponseEntity<com.funding.backend.global.utils.ApiResponse<Page<ProjectInfoResponseDto>>> getPopularProjects(
            @RequestParam ProjectTypeFilter projectType,
            @RequestParam PopularProjectSortType sortType,
            @ParameterObject Pageable pageable
    ) {
        Page<ProjectInfoResponseDto> response = projectService.getPopularProjects(projectType, sortType, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "사용자의 모든 알림 조회 성공", response));
    }
}
