package com.coupleapp.aiservice.dto;
import lombok.Data;
import java.util.List;
@Data
public class RecommendationRequest {
    private List<String> interests;
    private String occasion;
    private String budget;
    private String context;
}
