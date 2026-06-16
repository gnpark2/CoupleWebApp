package com.coupleapp.common.dto;
import lombok.*;
import java.time.Instant;
import java.util.UUID;
@Data @Builder public class CharacterXpEvent { private UUID coupleId; private UUID triggeredByUserId; private String interactionType; private int xpGained; private Instant timestamp; }
