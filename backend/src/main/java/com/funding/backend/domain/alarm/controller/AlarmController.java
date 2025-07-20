package com.funding.backend.domain.alarm.controller;




import com.funding.backend.domain.alarm.service.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/alarm")
@Tag(name = "알림 컨트롤러")
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;

    @Tag(name = "SSE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2000", description = "SSE 연결 성공"),
            @ApiResponse(responseCode = "5000", description = "SSE 연결 실패")
    })
    @Operation(summary = "SSE 연결")
    @GetMapping(value="/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @RequestHeader(value="Last-Event-ID", required = false, defaultValue = "") String lastEventId ){

        return notificationService.subscribe(userDetails.getMember().getId(), lastEventId);
    }


}
