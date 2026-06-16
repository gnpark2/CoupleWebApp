package com.coupleapp.userservice.security;
import lombok.*; import java.util.UUID;
@Getter @AllArgsConstructor public class AuthenticatedUser {private final UUID userId;private final UUID coupleId;}
