package com.coupleapp.aiservice.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;
@Data
public class AiRequest {
    @NotBlank
    private String feature;
    private String context;
    private List<String> entries;
    private List<String> moods;
}
