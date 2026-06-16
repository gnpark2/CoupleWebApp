package com.coupleapp.authservice.dto;
import lombok.*;
import java.util.UUID;
@Data @AllArgsConstructor public class AuthResponse { private String accessToken; private String refreshToken; private UUID userId; private String nickname; }
