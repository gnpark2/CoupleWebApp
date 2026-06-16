package com.coupleapp.characterservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCharacterRequest {
    @Size(min = 1, max = 30)
    private String name;
    private String avatarEmoji;
    private String theme;
}
