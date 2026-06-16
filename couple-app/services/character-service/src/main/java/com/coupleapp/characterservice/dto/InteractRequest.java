package com.coupleapp.characterservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InteractRequest {
    // feed | play | pat | rest
    @NotBlank
    private String action;
}
