package com.coupleapp.common.dto;
import lombok.*;
import java.time.Instant;
import java.util.UUID;
@Data @Builder public class FeelingSharedEvent { private UUID coupleId; private UUID userId; private String moodEmoji; private String moodLabel; private String comment; private Instant sharedAt; }
