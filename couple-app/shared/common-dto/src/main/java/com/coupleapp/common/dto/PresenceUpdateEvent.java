package com.coupleapp.common.dto;
import lombok.*;
import java.time.Instant;
import java.util.UUID;
@Data @Builder public class PresenceUpdateEvent { private UUID userId; private UUID coupleId; private boolean online; private String location; private Instant timestamp; }
