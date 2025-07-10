package com.funding.backend.domain.user.service;

import com.funding.backend.domain.user.dto.response.UserInfoResponse;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
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

    public User findUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }
}
