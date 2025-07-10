package com.funding.backend.domain.qna.controller;

import com.funding.backend.domain.qna.dto.request.AnswerRequestDto;
import com.funding.backend.domain.qna.dto.request.QnaRequestDto;
import com.funding.backend.domain.qna.dto.response.AnswerResponseDto;
import com.funding.backend.domain.qna.dto.response.QnaResponseDto;
import com.funding.backend.domain.qna.service.QnaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<Page<QnaResponseDto>> getAllQna(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Long currentUserId){

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<QnaResponseDto> qnaPage = qnaService.findAllQna(pageable, currentUserId);

        return ResponseEntity.ok(qnaPage);
    }

    //QnA 상세 조회
    @Operation(summary = "QnA 상세조회", description = "qnaId로 특정 QnA 조회")
    @GetMapping("/{qnaId}")
    public ResponseEntity<QnaResponseDto> getQnaById(@PathVariable Long qnaId,
                                                     @RequestParam(required = false) Long currentUserId){

        QnaResponseDto qna = qnaService.findByQnaId(qnaId, currentUserId);
        return  ResponseEntity.ok(qna);
    }

    //프로젝트별 QnA 조회
    @Operation(summary = "프로젝트별 QnA 조회", description = "특정 프로젝트의 Qna 조회")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<QnaResponseDto>> getQnaByProject(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) long currentUserId){

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<QnaResponseDto> qnaPage =
                qnaService.findQnaByProjectId(projectId, pageable, currentUserId);
        return  ResponseEntity.ok(qnaPage);
    }

    //사용자별 QnA 조회
    @Operation(summary = "사용자별 QnA 조회", description = "특정 사용자의 QnA를 조회")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<QnaResponseDto>> getQnaByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Long currentUserId){

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<QnaResponseDto> qnaPage = qnaService.findQnaByUserId(userId, pageable, currentUserId);

        return ResponseEntity.ok(qnaPage);
    }

    //QnA 작성
    @Operation(summary = "QnA 작성", description = "새로운 QnA를 작성")
    @PostMapping
    public ResponseEntity<QnaResponseDto> createQna(@Valid @RequestBody QnaRequestDto requestDto){

        QnaResponseDto result = qnaService.createQna(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    //QnA 수정
    @Operation(summary = "QnA 수정", description = "기존 QnA를 수정")
    @PutMapping("/{qnaId}")
    public ResponseEntity<QnaResponseDto> updateQna(@PathVariable Long qnaId,
                                                    @Valid @RequestBody QnaRequestDto requestDto){

        QnaResponseDto result = qnaService.updateQna(qnaId, requestDto);
        return ResponseEntity.ok(result);
    }

    //QnA 삭제
    @Operation(summary = "QnA 삭제", description = "QnA 삭제")
    @DeleteMapping("/{qnaId}")
    public ResponseEntity<Void> deleteQna(@PathVariable Long qnaId, @RequestParam Long userId){

        qnaService.deleteQna(qnaId, userId);
        return ResponseEntity.noContent().build();
    }

    /*
    답변 관련 API
     */

    //QnA 답변 작성
    @Operation(summary = "QnA 답변 작성", description = "QnA에 답변 작성")
    @PostMapping("/{qnaId}/answer")
    public ResponseEntity<AnswerResponseDto> createAnswer(@PathVariable Long qnaId,
                                                          @Valid @RequestBody AnswerRequestDto requestDto){

        AnswerResponseDto result = qnaService.createAnswer(qnaId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    //QnA 답변 수정
    @Operation(summary = "QnA 답변 수정", description = "QnA에 답변 수정")
    @PutMapping("/{qnaId}/answer")
    public ResponseEntity<AnswerResponseDto> updateAnswer(@PathVariable Long qnaId,
                                                          @Valid @RequestBody AnswerRequestDto requestDto){

        AnswerResponseDto result = qnaService.updateAnswer(qnaId, requestDto);
        return  ResponseEntity.ok(result);
    }

    //QnA 답변 삭제
    @Operation(summary = "QnA 답변 삭제", description = "QnA 답변 삭제")
    @DeleteMapping("/{qnaId}/answer")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long qnaId){

        qnaService.deleteAnswer(qnaId);
        return ResponseEntity.noContent().build();
    }
}
