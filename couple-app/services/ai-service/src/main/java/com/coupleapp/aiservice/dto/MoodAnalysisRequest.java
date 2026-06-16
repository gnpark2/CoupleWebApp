package com.coupleapp.aiservice.dto;
import lombok.Data;
import java.util.List;
@Data
public class MoodAnalysisRequest {
    private List<String> myMoods;
    private List<String> partnerMoods;
}
