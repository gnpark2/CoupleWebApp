package com.coupleapp.aiservice.service;
import com.coupleapp.aiservice.dto.AiResponse;
import com.coupleapp.aiservice.dto.MoodAnalysisRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class MoodAnalysisService {
    private final AiClient aiClient;
    private static final String SYS = "You are an empathetic relationship coach. Give a brief 2-3 sentence insight into a long-distance couple's mood trends and one gentle practical suggestion.";
    public AiResponse analyse(MoodAnalysisRequest req) {
        if ((req.getMyMoods()==null||req.getMyMoods().isEmpty())&&(req.getPartnerMoods()==null||req.getPartnerMoods().isEmpty()))
            return AiResponse.builder().feature("mood-analysis").result("Not enough mood data yet. Keep sharing your feelings daily!").fromStub(true).build();
        String prompt = String.format("Analyse mood trends. My moods: %s. Partner moods: %s.", fmt(req.getMyMoods()), fmt(req.getPartnerMoods()));
        String raw = aiClient.complete(SYS, prompt);
        if (raw == null) return AiResponse.builder().feature("mood-analysis").result(stubAnalysis(req.getMyMoods())).fromStub(true).build();
        return AiResponse.builder().feature("mood-analysis").result(raw.trim()).fromStub(false).build();
    }
    private String fmt(List<String> moods) {
        if (moods==null||moods.isEmpty()) return "not recorded";
        return moods.stream().collect(Collectors.groupingBy(m->m,Collectors.counting()))
                .entrySet().stream().map(e->e.getKey()+" x"+e.getValue()).collect(Collectors.joining(", "));
    }
    private String stubAnalysis(List<String> mine) {
        boolean positive = mine!=null&&mine.stream().anyMatch(m->m.contains("happy")||m.contains("love")||m.contains("calm"));
        if (positive) return "You and your partner seem to be in a warm, connected place lately. Your moods align more than you might realise. Keep the momentum going with a surprise message today.";
        return "Distance can wear on both of you and that is completely normal. Your moods show resilience even through the tough days. Try scheduling a longer video call this week just to talk with no agenda.";
    }
}
