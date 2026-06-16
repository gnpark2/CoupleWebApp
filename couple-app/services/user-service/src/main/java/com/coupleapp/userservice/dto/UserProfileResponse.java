package com.coupleapp.userservice.dto;
import lombok.*;
import java.util.UUID;
@Data @Builder public class UserProfileResponse{private UUID userId;private String nickname;private String bio;private String timezone;private String city;private String profileImageUrl;private UUID coupleId;private boolean paired;}
