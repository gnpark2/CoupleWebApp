package com.coupleapp.aiservice.controller;
import com.coupleapp.aiservice.dto.*;
import com.coupleapp.aiservice.security.AuthenticatedUser;
import com.coupleapp.aiservice.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiOrchestrator        orchestrator;
    private final RecommendationService recommendationService;
    private final DateIdeaService       dateIdeaService;
    private final DiarySummaryService   diarySummaryService;
    private final MoodAnalysisService   moodAnalysisService;

    @PostMapping("/ask")
    public ResponseEntity<AiResponse> ask(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody AiRequest req) {
        return ResponseEntity.ok(orchestrator.handle(req.getFeature(), req));
    }

    @PostMapping("/recommend")
    public ResponseEntity<AiResponse> recommend(@AuthenticationPrincipal AuthenticatedUser user, @RequestBody RecommendationRequest req) {
        return ResponseEntity.ok(recommendationService.recommend(req));
    }

    @PostMapping("/date-ideas")
    public ResponseEntity<AiResponse> dateIdeas(@AuthenticationPrincipal AuthenticatedUser user, @RequestBody DateIdeaRequest req) {
        return ResponseEntity.ok(dateIdeaService.generateIdeas(req));
    }

    @PostMapping("/summarize")
    public ResponseEntity<AiResponse> summarize(@AuthenticationPrincipal AuthenticatedUser user, @RequestBody SummarizeRequest req) {
        return ResponseEntity.ok(diarySummaryService.summarize(req));
    }

    @PostMapping("/mood-analysis")
    public ResponseEntity<AiResponse> moodAnalysis(@AuthenticationPrincipal AuthenticatedUser user, @RequestBody MoodAnalysisRequest req) {
        return ResponseEntity.ok(moodAnalysisService.analyse(req));
    }
}
