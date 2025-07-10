package com.funding.backend.domain.user.controller;

import com.funding.backend.domain.user.dto.request.UserAccountInfo;
import com.funding.backend.domain.user.dto.request.UserProfileUpdateRequest;
import com.funding.backend.domain.user.dto.response.UserInfoResponse;
import com.funding.backend.domain.user.dto.response.UserProfileResponse;
import com.funding.backend.domain.user.email.service.EmailService;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.RefreshTokenService;
import com.funding.backend.security.jwt.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "회원 정보 API")
public class UserController {

    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailService emailService;

    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 사용자 정보 조회", description = "사용자의 정보를 조회합니다.")
    public ResponseEntity<UserProfileResponse> getMyPage() {
        Long userId = tokenService.getUserIdFromAccessToken();
        return ResponseEntity.ok(userService.getMyProfile(userId));
    }

    @PutMapping(value = "/mypage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "마이페이지 사용자 정보 수정", description = "사용자의 정보를 수정합니다.")
    public ResponseEntity<Void> updateMyPage(@RequestPart("info") UserProfileUpdateRequest request,
                                             @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        Long userId = tokenService.getUserIdFromAccessToken();
        userService.updateUserProfile(userId, request, imageFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-email")
    @Operation(summary = "이메일 중복 확인", description = "사용자의 이메일 정보를 수정시 중복 확인을 합니다.")
    public ResponseEntity<Void> checkEmail(@RequestParam(name = "email") String email) {
        boolean exists = userService.checkEmailDuplication(email);
        if (exists) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_ALREADY_EXISTS);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-verification")
    @Operation(summary = "이메일 인증 코드 메일 전송", description = "사용자의 이메일 정보를 수정시 이메일 인증 확인을 위해 인증 코드 메일을 전송합니다.")
    public ResponseEntity<Void> sendVerification(@RequestParam(name = "email") String email) {
        emailService.sendVerificationCode(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    @Operation(summary = "이메일 인증 코드 검증", description = "사용자의 이메일 정보를 수정시 입력한 이메일 인증 코드를 검증합니다.")
    public ResponseEntity<Boolean> verifyCode(@RequestParam(name = "email") String email,
                                              @RequestParam(name = "code") String code) {
        boolean result = emailService.verifyCode(email, code);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/mypage/account")
    @Operation(summary = "은행 정보 및 계좌 조회", description = "현재 로그인한 사용자의 은행명과 계좌번호를 조회합니다.")
    public ResponseEntity<UserAccountInfo> getAccountInfo() {
        Long userId = tokenService.getUserIdFromAccessToken();
        UserAccountInfo response = userService.getBankInfo(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/mypage/account")
    @Operation(summary = "은행 정보 및 계좌 등록/수정", description = "사용자의 은행명과 계좌번호를 등록하거나 수정합니다.")
    public ResponseEntity<Void> updateAccountInfo(@RequestBody UserAccountInfo request) {
        Long userId = tokenService.getUserIdFromAccessToken();
        userService.updateBankInfo(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/mypage/account")
    @Operation(summary = "은행 정보 및 계좌 삭제", description = "사용자의 은행명과 계좌번호를 삭제합니다.")
    public ResponseEntity<Void> deleteAccountInfo() {
        Long userId = tokenService.getUserIdFromAccessToken();
        userService.deleteBankInfo(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(summary = "사용자 정보 전체 조회", description = "사용자의 모든 정보를 조회합니다.")
    public ResponseEntity<UserInfoResponse> getMyInfo(HttpServletRequest request) {
        Long userId = tokenService.getUserIdFromAccessToken();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        UserInfoResponse response = UserInfoResponse.from(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "쿠키/토큰 삭제")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. accessToken에서 userId 추출
        Long userId = tokenService.getUserIdFromAccessToken();

        // 2. Redis에서 refreshToken 삭제
        refreshTokenService.deleteRefreshToken(userId);

        // 3. accessToken 쿠키 삭제
        tokenService.deleteCookie("accessToken");

        return ResponseEntity.ok().build();
    }
}