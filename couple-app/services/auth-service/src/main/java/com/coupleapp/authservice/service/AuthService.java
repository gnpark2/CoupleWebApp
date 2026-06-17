package com.coupleapp.authservice.service;
import com.coupleapp.authservice.domain.User;
import com.coupleapp.authservice.dto.*;
import com.coupleapp.authservice.repository.UserRepository;
import com.coupleapp.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.util.UUID;

@Service @RequiredArgsConstructor public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redis;

    @Transactional public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) throw new IllegalArgumentException("Email already registered");
        User user = User.builder().email(req.getEmail().toLowerCase()).passwordHash(passwordEncoder.encode(req.getPassword())).nickname(req.getNickname()).build();
        userRepository.save(user);
        return issueTokens(user);
    }

    @Transactional(readOnly=true) public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail().toLowerCase()).orElseThrow(()->new IllegalArgumentException("Invalid email or password"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) throw new IllegalArgumentException("Invalid email or password");
        return issueTokens(user);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) throw new IllegalArgumentException("Invalid refresh token");
        UUID uid = jwtProvider.getUserId(refreshToken);
        String stored = redis.opsForValue().get("auth:refresh:" + uid);
        if (!refreshToken.equals(stored)) throw new IllegalArgumentException("Token revoked");
        User user = userRepository.findById(uid).orElseThrow(()->new IllegalArgumentException("User not found"));
        return issueTokens(user);
    }

    public void logout(String accessToken) {
        if (!jwtProvider.validateToken(accessToken)) return;
        UUID uid = jwtProvider.getUserId(accessToken);
        redis.opsForValue().set("auth:blacklist:" + accessToken, "1", Duration.ofMinutes(15));
        redis.delete("auth:refresh:" + uid);
    }

    private AuthResponse issueTokens(User user) {
        String access = jwtProvider.generateAccessToken(user.getId(), user.getCoupleId());
        String refresh = jwtProvider.generateRefreshToken(user.getId());
        redis.opsForValue().set("auth:refresh:" + user.getId(), refresh, Duration.ofDays(30));
        return new AuthResponse(access, refresh, user.getId(), user.getNickname());
    }

    public void setCoupleId(UUID userId, UUID coupleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setCoupleId(coupleId);
        userRepository.save(user);
    }
}
