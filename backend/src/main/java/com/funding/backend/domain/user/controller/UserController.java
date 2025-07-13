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
import com.funding.backend.global.utils.ApiResponse;
import com.funding.backend.security.jwt.RefreshTokenService;
import com.funding.backend.security.jwt.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyPage() {
        Long userId = tokenService.getUserIdFromAccessToken();
        UserProfileResponse response = userService.getMyProfile(userId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "마이페이지 정보 조회 성공", response));
    }

    @PutMapping(value = "/mypage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "마이페이지 사용자 정보 수정", description = "사용자의 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateMyPage(@RequestPart("info") UserProfileUpdateRequest request,
                                                          @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        Long userId = tokenService.getUserIdFromAccessToken();
        userService.updateUserProfile(userId, request, imageFile);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "마이페이지 수정 성공"));
    }

    @GetMapping("/check-email")
    @Operation(summary = "이메일 중복 확인", description = "사용자의 이메일 정보를 수정시 중복 확인을 합니다.")
    public ResponseEntity<ApiResponse<Void>> checkEmail(@RequestParam(name = "email") String email) {
        boolean exists = userService.checkEmailDuplication(email);
        if (exists) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_ALREADY_EXISTS);
        }
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "사용 가능한 이메일입니다."));
    }

    @PostMapping("/send-verification")
    @Operation(summary = "이메일 인증 코드 메일 전송", description = "사용자의 이메일 정보를 수정시 이메일 인증 확인을 위해 인증 코드 메일을 전송합니다.")
    public ResponseEntity<ApiResponse<Void>> sendVerification(@RequestParam(name = "email") String email) {
        emailService.sendVerificationCode(email);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "인증 코드 전송 성공"));
    }

    @PostMapping("/verify-code")
    @Operation(summary = "이메일 인증 코드 검증", description = "사용자의 이메일 정보를 수정시 입력한 이메일 인증 코드를 검증합니다.")
    public ResponseEntity<ApiResponse<Boolean>> verifyCode(@RequestParam(name = "email") String email,
                                                           @RequestParam(name = "code") String code) {
        boolean result = emailService.verifyCode(email, code);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "이메일 인증 성공", result));
    }

    @GetMapping("/mypage/account")
    @Operation(summary = "은행 정보 및 계좌 조회", description = "현재 로그인한 사용자의 은행명과 계좌번호를 조회합니다.")
    public ResponseEntity<ApiResponse<UserAccountInfo>> getAccountInfo() {
        Long userId = tokenService.getUserIdFromAccessToken();
        UserAccountInfo response = userService.getBankInfo(userId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "계좌 정보 조회 성공", response));
    }

    @PutMapping("/mypage/account")
    @Operation(summary = "은행 정보 및 계좌 등록/수정", description = "사용자의 은행명과 계좌번호를 등록하거나 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateAccountInfo(@RequestBody UserAccountInfo request) {
        Long userId = tokenService.getUserIdFromAccessToken();
        userService.updateBankInfo(userId, request);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "계좌 정보 수정 성공"));
    }

    @DeleteMapping("/mypage/account")
    @Operation(summary = "은행 정보 및 계좌 삭제", description = "사용자의 은행명과 계좌번호를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteAccountInfo() {
        Long userId = tokenService.getUserIdFromAccessToken();
        userService.deleteBankInfo(userId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "계좌 정보 삭제 성공"));
    }

    @GetMapping("/me")
    @Operation(summary = "사용자 정보 전체 조회", description = "사용자의 모든 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo() {
        Long userId = tokenService.getUserIdFromAccessToken();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        UserInfoResponse response = UserInfoResponse.from(user);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "사용자 정보 조회 성공", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "쿠키/토큰 삭제")
    public ResponseEntity<ApiResponse<Void>> logout() {
        Long userId = tokenService.getUserIdFromAccessToken();

        // Redis에서 refreshToken 삭제
        refreshTokenService.deleteRefreshToken(userId);
        // accessToken 쿠키 삭제
        tokenService.deleteCookie("accessToken");

        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "로그아웃 성공"));
    }

    @GetMapping("/profile/{userId}")
    @Operation(summary = "사용자의 프로필 조회 ", description = "사용자의 프로필을 조회할 수 있습니다. ")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(@PathVariable Long userId) {
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "사용자 프로필 조회", response));
    }

}