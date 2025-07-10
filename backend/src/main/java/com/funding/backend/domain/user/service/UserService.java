package com.funding.backend.domain.user.service;

import com.funding.backend.domain.user.dto.request.UserAccountInfo;
import com.funding.backend.domain.user.dto.request.UserProfileUpdateRequest;
import com.funding.backend.domain.user.dto.response.UserInfoResponse;
import com.funding.backend.domain.user.dto.response.UserProfileResponse;
import com.funding.backend.domain.user.email.service.EmailService;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.global.utils.s3.S3Uploader;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final EmailService emailService;

    public UserProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        return UserProfileResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .introduce(user.getIntroduce())
                .portfolioAddress(user.getPortfolioAddress())
                .image(user.getImage())
                .build();
    }

    public void updateUserProfile(Long userId, UserProfileUpdateRequest request, MultipartFile imageFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 이메일 변경이 있는 경우
        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("이미 사용 중인 이메일입니다.");
            }
            if (!emailService.isVerified(request.getEmail())) {
                throw new RuntimeException("이메일 인증이 필요합니다.");
            }

            user.setEmail(request.getEmail()); // 인증된 경우만 저장
        }

        // 이미지가 새로 업로드된 경우
        if (imageFile != null && !imageFile.isEmpty()) {
            // 이전 이미지 삭제
            if (user.getImage() != null) {
                s3Uploader.deleteFile(user.getImage());
            }

            try {
                String imageUrl = s3Uploader.uploadFile(imageFile);
                user.setImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 실패");
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
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional(readOnly = true)
    public UserAccountInfo getBankInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        return new UserAccountInfo(user.getBank(), user.getAccount());
    }

    @Transactional
    public void updateBankInfo(Long userId, UserAccountInfo request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        user.setBank(request.getBank());
        user.setAccount(request.getAccount());
    }

    @Transactional
    public void deleteBankInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        user.setBank(null);
        user.setAccount(null);
    }
}
