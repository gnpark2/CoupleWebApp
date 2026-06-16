package com.coupleapp.aiservice.service;
import com.coupleapp.aiservice.dto.AiResponse;
import com.coupleapp.aiservice.dto.DateIdeaRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class DateIdeaService {
    private final AiClient aiClient;
    private static final String SYS = "You are a creative date planner for long-distance couples. Respond with a JSON array of exactly 5 date idea strings. Return only the JSON array.";
    public AiResponse generateIdeas(DateIdeaRequest req) {
        String prompt = String.format("Suggest %s date ideas for a couple. One lives in %s, the other in %s. Interests: %s.",
                req.getDateType()!=null?req.getDateType():"virtual",
                req.getMyCity()!=null?req.getMyCity():"one city",
                req.getPartnerCity()!=null?req.getPartnerCity():"another city",
                req.getSharedInterests()!=null?String.join(", ",req.getSharedInterests()):"general");
        String raw = aiClient.complete(SYS, prompt);
        if (raw == null) return AiResponse.builder().feature("date-ideas").items(stubIdeas(req.getDateType())).fromStub(true).build();
        return AiResponse.builder().feature("date-ideas").items(List.of(raw)).fromStub(false).build();
    }
    private List<String> stubIdeas(String type) {
        if ("in-person".equalsIgnoreCase(type))
            return List.of("Visit a rooftop cafe and watch the sunset","Explore a local night market together","Cook the same recipe simultaneously on video call","Take a pottery or painting class","Plan a weekend trip to meet halfway");
        return List.of("Watch the same movie simultaneously","Play Codenames or Gartic Phone online","Do a virtual museum tour of the Louvre","Cook the same recipe on video call and rate each other","Take turns reading a book chapter aloud");
    }
}
