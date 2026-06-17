package com.coupleapp.authservice.controller;
import com.coupleapp.authservice.dto.*;
import com.coupleapp.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register") public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) { return ResponseEntity.status(201).body(authService.register(req)); }
    @PostMapping("/login") public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) { return ResponseEntity.ok(authService.login(req)); }
    @PostMapping("/refresh") public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest req) { return ResponseEntity.ok(authService.refresh(req.getRefreshToken())); }
    @PostMapping("/logout") public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearer) { authService.logout(bearer.replace("Bearer ","")); return ResponseEntity.noContent().build(); }

    @PostMapping("/internal/set-couple")
    public ResponseEntity<Void> setCouple(
            @RequestParam String userId,
            @RequestParam String coupleId) {
        authService.setCoupleId(
            java.util.UUID.fromString(userId),
            java.util.UUID.fromString(coupleId));
        return ResponseEntity.ok().build();
    }
}
