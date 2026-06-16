package com.coupleapp.aiservice.service;
import com.coupleapp.aiservice.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class AiOrchestrator {
    private final RecommendationService recommendationService;
    private final DateIdeaService       dateIdeaService;
    private final DiarySummaryService   diarySummaryService;
    private final MoodAnalysisService   moodAnalysisService;
    public AiResponse handle(String feature, AiRequest req) {
        return switch (feature) {
            case "recommend" -> { var r=new RecommendationRequest(); r.setContext(req.getContext()); yield recommendationService.recommend(r); }
            case "date-ideas" -> { var r=new DateIdeaRequest(); r.setContext(req.getContext()); yield dateIdeaService.generateIdeas(r); }
            case "summarize" -> { var r=new SummarizeRequest(); r.setEntries(req.getEntries()); r.setPeriod(req.getContext()); yield diarySummaryService.summarize(r); }
            case "mood-analysis" -> { var r=new MoodAnalysisRequest(); r.setMyMoods(req.getMoods()); yield moodAnalysisService.analyse(r); }
            default -> throw new IllegalArgumentException("Unknown feature: "+feature+". Use: recommend, date-ideas, summarize, mood-analysis");
        };
    }
}
