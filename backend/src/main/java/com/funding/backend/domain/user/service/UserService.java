package com.funding.backend.domain.user.service;

import com.funding.backend.domain.user.dto.response.UserInfoResponse;
import com.funding.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserInfoResponse getUserInfo(Long userId) {
        return userRepository.findById(userId)
                .map(UserInfoResponse::from)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
