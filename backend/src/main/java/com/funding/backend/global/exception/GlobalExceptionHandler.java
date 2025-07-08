//package com.funding.backend.global.exception;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@Slf4j
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(BusinessLogicException.class)
//    public ResponseEntity<ErrorResponse> handleBusinessLogicException(BusinessLogicException ex) {
//        ExceptionCode code = ex.getExceptionCode();
//
//        // ✅ 서버 로그에 예외 전체 출력
//        log.error("[BusinessLogicException] {} - {}", code.name(), code.getMessage(), ex);
//
//        return ResponseEntity
//                .status(code.getStatus())
//                .body(new ErrorResponse(code.getStatus(), code.getMessage()));
//    }
//}