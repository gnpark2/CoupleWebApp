package com.coupleapp.aiservice.service;
import com.coupleapp.aiservice.dto.AiResponse;
import com.coupleapp.aiservice.dto.RecommendationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final AiClient aiClient;
    private static final String SYS = "You are a thoughtful gift advisor for long-distance couples. Respond with a JSON array of exactly 5 short recommendation strings. Return only the JSON array.";
    public AiResponse recommend(RecommendationRequest req) {
        String prompt = String.format("Suggest gifts for a long-distance couple. Occasion: %s. Budget: %s. Interests: %s.",
                req.getOccasion()!=null?req.getOccasion():"just because",
                req.getBudget()!=null?req.getBudget():"any",
                req.getInterests()!=null?String.join(", ",req.getInterests()):"not specified");
        String raw = aiClient.complete(SYS, prompt);
        if (raw == null) return AiResponse.builder().feature("recommend").items(stubRecs(req.getOccasion())).fromStub(true).build();
        return AiResponse.builder().feature("recommend").items(List.of(raw)).fromStub(false).build();
    }
    private List<String> stubRecs(String occasion) {
        if ("anniversary".equalsIgnoreCase(occasion))
            return List.of("Handwritten letter in a beautiful envelope","Order their favourite food delivered","Create a shared photo book","Plan a virtual candlelight dinner","Gift a personalised star map of the night you met");
        return List.of("A cozy care package with snacks","Matching couple bracelets with coordinates","Streaming service subscription to watch together","Custom illustrated portrait of you both","A heartfelt voice message keepsake");
    }
}
