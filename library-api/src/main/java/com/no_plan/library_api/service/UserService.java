package com.no_plan.library_api.service;

import com.no_plan.library_api.dto.UserResponse;
import com.no_plan.library_api.dto.UserUpdateRequest;
import com.no_plan.library_api.entity.User;
import com.no_plan.library_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new IllegalArgumentException(("사용자를 찾을 수 없음")));

        return UserResponse.from(user);
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponse::from);
    }

    @Transactional
    public UserResponse updateUserInfo(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new IllegalArgumentException("사용자를 찾을 수 없음"));

        String encodedPassword = null;
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            encodedPassword = passwordEncoder.encode(request.getPassword());
        }

        user.updateInfo(
                encodedPassword,
                request.getName(),
                request.getPhoneNum(),
                request.getEmail()
        );

        return UserResponse.from(user);
    }

    @Transactional
    public void withdrawUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new IllegalArgumentException("사용자를 찾을 수 없음"));

        userRepository.delete(user);
    }

    @Transactional
    public void updateUserRole(Long userId, Boolean isAdmin) {
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new IllegalArgumentException("사용자를 찾을 수 없음"));

        user.changeRole(isAdmin);
    }
}
