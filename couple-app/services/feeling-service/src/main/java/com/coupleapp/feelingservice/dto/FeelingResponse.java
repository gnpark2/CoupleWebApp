package com.coupleapp.feelingservice.dto;
import lombok.*;
import java.time.Instant;
import java.util.UUID;
@Data @Builder public class FeelingResponse{private UUID id;private UUID userId;private UUID coupleId;private String moodEmoji;private String moodLabel;private String comment;private Instant createdAt;}
