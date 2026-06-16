package com.coupleapp.characterservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CharacterResponse {
    private UUID id;
    private UUID coupleId;
    private String name;
    private int xp;
    private int level;
    private int xpToNextLevel;
    private int happiness;
    private int hunger;
    private int energy;
    private String avatarEmoji;
    private String theme;
    private String statusMessage;
    private Instant updatedAt;
}
