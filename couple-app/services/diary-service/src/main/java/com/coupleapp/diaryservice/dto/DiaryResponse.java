package com.coupleapp.diaryservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class DiaryResponse {
    private UUID id;
    private UUID coupleId;
    private UUID authorId;
    private String entryType;
    private String title;
    private String content;
    private LocalDate entryDate;
    private Instant createdAt;
    private Instant updatedAt;
}
