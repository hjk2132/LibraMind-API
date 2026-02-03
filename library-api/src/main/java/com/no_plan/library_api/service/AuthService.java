package com.no_plan.library_api.service;

import com.no_plan.library_api.dto.LoginRequest;
import com.no_plan.library_api.dto.RefreshTokenRequest;
import com.no_plan.library_api.dto.SignupRequest;
import com.no_plan.library_api.dto.TokenResponse;
import com.no_plan.library_api.entity.TokenBlacklist;
import com.no_plan.library_api.entity.User;
import com.no_plan.library_api.repository.TokenBlacklistRepository;
import com.no_plan.library_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.no_plan.library_api.security.JwtTokenProvider;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistRepository tokenBlackListRepository;

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByLoginId(request.getId())) {
            throw new IllegalStateException("이미 존재하는 아이디");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일");
        }

        User user = User.builder()
                .loginId(request.getId())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phoneNum(request.getPhoneNum())
                .email(request.getEmail())
                .isAdmin(false)
                .build();

        userRepository.save(user);
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.getId())
                .orElseThrow( () -> new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않음"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않음");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getIsAdmin());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();
    }

    @Transactional
    public void logout(String tokenHeader, String refreshToken) {
        // Access Token 처리
        String accessToken = resolveToken(tokenHeader);
        blockToken(accessToken);

        // Refresh Token 처리 (null 체크 필수)
        if (refreshToken != null && !refreshToken.isBlank()) {
            blockToken(refreshToken);
        }
    }

    private void blockToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            return;
        }

        Long expiration = jwtTokenProvider.getExpiration(token);
        if (expiration > 0) {
            TokenBlacklist blacklist = TokenBlacklist.builder()
                    .token(token)
                    .expiresAt(LocalDateTime.now().plus(expiration, ChronoUnit.MILLIS))
                    .build();

            tokenBlackListRepository.save(blacklist);
        }
    }

    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        // 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(requestRefreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token");
        }

        // 블랙리스트(로그아웃) 확인
        if (tokenBlackListRepository.existsByToken(requestRefreshToken)) {
            throw new IllegalArgumentException("로그아웃된 토큰");
        }

        Long userId = jwtTokenProvider.getUserId(requestRefreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow( () ->
                        new IllegalArgumentException("사용자를 찾을 수 없음"));
        long expirationTime = jwtTokenProvider.getExpiration(requestRefreshToken);

        if (expirationTime > 0) {
            TokenBlacklist usedToken = TokenBlacklist.builder()
                    .token(requestRefreshToken)
                    .expiresAt(LocalDateTime.now().plus(expirationTime, ChronoUnit.MILLIS))
                    .build();
            tokenBlackListRepository.save(usedToken);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getIsAdmin());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();
    }

    private String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return bearerToken;
    }
}