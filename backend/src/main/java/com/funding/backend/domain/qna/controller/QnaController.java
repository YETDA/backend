package com.funding.backend.domain.qna.controller;

import com.funding.backend.domain.qna.dto.request.AnswerRequestDto;
import com.funding.backend.domain.qna.dto.request.QnaRequestDto;
import com.funding.backend.domain.qna.dto.response.AnswerResponseDto;
import com.funding.backend.domain.qna.dto.response.QnaResponseDto;
import com.funding.backend.domain.qna.service.QnaService;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/qna")
@RequiredArgsConstructor
@Tag(name = "QnA 관리", description = "QnA CRUD API")
public class QnaController {

    private final QnaService qnaService;

    //QnA 전체조회
    @Operation(summary = "QnA 전체조회", description = "모든 QnA를 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<QnaResponseDto>>> getAllQna(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<QnaResponseDto> response = qnaService.findAllQna(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "QnA 전체 조회 성공", response));
    }

    //QnA 상세 조회
    @Operation(summary = "QnA 상세조회", description = "qnaId로 특정 QnA 조회")
    @GetMapping("/{qnaId}")
    public ResponseEntity<ApiResponse<QnaResponseDto>> getQnaById(@PathVariable Long qnaId){

        QnaResponseDto response = qnaService.findByQnaId(qnaId);
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "QnA 상세 조회 성공", response));
    }

    //프로젝트별 QnA 조회
    @Operation(summary = "프로젝트별 QnA 조회", description = "특정 프로젝트의 Qna 조회")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<Page<QnaResponseDto>>> getQnaByProject(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<QnaResponseDto> response = qnaService.findQnaByProjectId(projectId, pageable);

        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "프로젝트별 QnA 조회 성공", response));
    }

    //사용자별 QnA 조회
    @Operation(summary = "사용자별 QnA 조회", description = "특정 사용자의 QnA를 조회")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<QnaResponseDto>>> getQnaByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<QnaResponseDto> response = qnaService.findQnaByUserId(userId, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "사용자별 QnA 조회 성공", response));
    }

    //QnA 작성
    @Operation(summary = "QnA 작성", description = "새로운 QnA를 작성")
    @PostMapping
    public ResponseEntity<ApiResponse<QnaResponseDto>> createQna(@Valid @RequestBody QnaRequestDto requestDto){

        QnaResponseDto response = qnaService.createQna(requestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "QnA 작성 성공", response));
    }

    //QnA 수정
    @Operation(summary = "QnA 수정", description = "기존 QnA를 수정")
    @PutMapping("/{qnaId}")
    public ResponseEntity<ApiResponse<QnaResponseDto>> updateQna(@PathVariable Long qnaId,
                                                    @Valid @RequestBody QnaRequestDto requestDto){

        QnaResponseDto response = qnaService.updateQna(qnaId, requestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "QnA 수정 성공", response));
    }

    //QnA 삭제
    @Operation(summary = "QnA 삭제", description = "QnA 삭제")
    @DeleteMapping("/{qnaId}")
    public ResponseEntity<ApiResponse<Void>> deleteQna(@PathVariable Long qnaId){

        qnaService.deleteQna(qnaId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "QnA 삭제 성공"));
    }

    /*
    답변 관련 API
     */

    //QnA 답변 작성
    @Operation(summary = "QnA 답변 작성", description = "QnA에 답변 작성")
    @PostMapping("/{qnaId}/answer")
    public ResponseEntity<ApiResponse<AnswerResponseDto>> createAnswer(@PathVariable Long qnaId,
                                                          @Valid @RequestBody AnswerRequestDto requestDto){

        AnswerResponseDto response = qnaService.createAnswer(qnaId, requestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "답변 작성 성공", response));
    }

    //QnA 답변 수정
    @Operation(summary = "QnA 답변 수정", description = "QnA에 답변 수정")
    @PutMapping("/{qnaId}/answer")
    public ResponseEntity<ApiResponse<AnswerResponseDto>> updateAnswer(@PathVariable Long qnaId,
                                                          @Valid @RequestBody AnswerRequestDto requestDto){

        AnswerResponseDto response = qnaService.updateAnswer(qnaId, requestDto);
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "답변 수정 성공", response));
    }

    //QnA 답변 삭제
    @Operation(summary = "QnA 답변 삭제", description = "QnA 답변 삭제")
    @DeleteMapping("/{qnaId}/answer")
    public ResponseEntity<ApiResponse<Void>> deleteAnswer(@PathVariable Long qnaId){

        qnaService.deleteAnswer(qnaId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "답변 삭제 성공"));
    }

}
