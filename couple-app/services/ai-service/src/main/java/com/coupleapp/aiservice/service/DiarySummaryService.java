package com.coupleapp.aiservice.service;
import com.coupleapp.aiservice.dto.AiResponse;
import com.coupleapp.aiservice.dto.SummarizeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class DiarySummaryService {
    private final AiClient aiClient;
    private static final String SYS = "You are a warm assistant that summarizes a couple's shared diary. Write a heartfelt 3-4 sentence summary in second person (You and your partner...).";
    public AiResponse summarize(SummarizeRequest req) {
        if (req.getEntries() == null || req.getEntries().isEmpty())
            return AiResponse.builder().feature("summarize").result("No entries to summarize yet. Start writing your story together!").fromStub(true).build();
        String combined = String.join("\n", req.getEntries());
        String prompt = String.format("Summarize these %s diary entries:%s",
                req.getPeriod()!=null?req.getPeriod():"recent",
                combined.substring(0, Math.min(combined.length(), 3000)));
        String raw = aiClient.complete(SYS, prompt);
        if (raw == null) return AiResponse.builder().feature("summarize").result(stubSummary(req.getEntries().size(), req.getPeriod())).fromStub(true).build();
        return AiResponse.builder().feature("summarize").result(raw.trim()).fromStub(false).build();
    }
    private String stubSummary(int count, String period) {
        return String.format("You and your partner shared %d entries this %s. Through the distance, your words kept you close - full of warmth, small moments, and the quiet promise of seeing each other soon. Every entry is a thread in the story you are writing together.", count, period!=null?period:"period");
    }
}
