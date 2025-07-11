package com.funding.backend.domain.user.service;

import com.funding.backend.domain.user.dto.request.UserAccountInfo;
import com.funding.backend.domain.user.dto.request.UserProfileUpdateRequest;
import com.funding.backend.domain.user.dto.response.UserInfoResponse;
import com.funding.backend.domain.user.dto.response.UserProfileResponse;
import com.funding.backend.domain.user.email.service.EmailService;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.utils.s3.S3Uploader;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final EmailService emailService;

    public UserProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        return UserProfileResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .introduce(user.getIntroduce())
                .portfolioAddress(user.getPortfolioAddress())
                .image(user.getImage())
                .build();
    }

    @Transactional
    public void updateUserProfile(Long userId, UserProfileUpdateRequest request, MultipartFile imageFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        // 이름 유효성 검사
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.NAME_CANNOT_BE_EMPTY);
        }

        // 이메일 변경
        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessLogicException(ExceptionCode.EMAIL_ALREADY_EXISTS);
            }
            if (!emailService.isVerified(request.getEmail())) {
                throw new BusinessLogicException(ExceptionCode.EMAIL_NOT_VERIFIED);
            }

            user.setEmail(request.getEmail());
        }

        // 이미지 업로드
        if (imageFile != null && !imageFile.isEmpty()) {
            if (user.getImage() != null) {
                s3Uploader.deleteFile(user.getImage());
            }

            try {
                String imageUrl = s3Uploader.uploadFile(imageFile);
                user.setImage(imageUrl);
            } catch (IOException e) {
                throw new BusinessLogicException(ExceptionCode.IMAGE_UPLOAD_FAILED);
            }
        }

        user.setName(request.getName());
        user.setIntroduce(request.getIntroduce());
        user.setPortfolioAddress(request.getPortfolioAddress());

        userRepository.save(user);
    }

    public boolean checkEmailDuplication(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserInfoResponse getUserInfo(Long userId) {
        return userRepository.findById(userId)
                .map(UserInfoResponse::from)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }

    public UserAccountInfo getBankInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        return new UserAccountInfo(user.getBank(), user.getAccount());
    }

    @Transactional
    public void updateBankInfo(Long userId, UserAccountInfo request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        // 은행명 또는 계좌번호 중 하나라도 null 또는 공란이면 예외 처리
        if (request.getBank() == null || request.getBank().trim().isEmpty()
                || request.getAccount() == null || request.getAccount().trim().isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.BANK_AND_ACCOUNT_REQUIRED);
        }

        user.setBank(request.getBank());
        user.setAccount(request.getAccount());
    }

    @Transactional
    public void deleteBankInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        user.setBank(null);
        user.setAccount(null);
    }

    public User findUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

    }
    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }
}
