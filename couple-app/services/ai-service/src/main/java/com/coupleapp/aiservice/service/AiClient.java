package com.coupleapp.aiservice.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;
@Slf4j
@Component
@RequiredArgsConstructor
public class AiClient {
    private final WebClient webClient;
    @Value("${ai.stub-mode:true}") private boolean stubMode;
    @Value("${ai.provider:anthropic}") private String provider;
    @Value("${ai.anthropic.api-key:skip-for-now}") private String anthropicKey;
    @Value("${ai.anthropic.model:claude-sonnet-4-6}") private String anthropicModel;
    @Value("${ai.openai.api-key:skip-for-now}") private String openaiKey;
    @Value("${ai.openai.model:gpt-4o-mini}") private String openaiModel;

    public String complete(String systemPrompt, String userPrompt) {
        if (stubMode || isKeyMissing()) {
            log.info("[AI STUB] feature prompt length={}", userPrompt.length());
            return null;
        }
        try {
            return "anthropic".equals(provider)
                    ? callAnthropic(systemPrompt, userPrompt)
                    : callOpenAi(systemPrompt, userPrompt);
        } catch (Exception e) {
            log.error("AI API call failed", e);
            return null;
        }
    }

    private String callAnthropic(String system, String user) {
        Map<String, Object> body = Map.of(
                "model", anthropicModel, "max_tokens", 1024,
                "system", system,
                "messages", List.of(Map.of("role", "user", "content", user)));
        Map<?, ?> res = webClient.post()
                .uri("https://api.anthropic.com/v1/messages")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("x-api-key", anthropicKey)
                .header("anthropic-version", "2023-06-01")
                .bodyValue(body).retrieve().bodyToMono(Map.class).block();
        if (res != null && res.get("content") instanceof List<?> c && !c.isEmpty()
                && c.get(0) instanceof Map<?, ?> b) return (String) b.get("text");
        return null;
    }

    private String callOpenAi(String system, String user) {
        Map<String, Object> body = Map.of("model", openaiModel,
                "messages", List.of(Map.of("role","system","content",system),
                                    Map.of("role","user","content",user)));
        Map<?, ?> res = webClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openaiKey)
                .bodyValue(body).retrieve().bodyToMono(Map.class).block();
        if (res != null && res.get("choices") instanceof List<?> ch && !ch.isEmpty()
                && ch.get(0) instanceof Map<?, ?> c0
                && c0.get("message") instanceof Map<?, ?> msg) return (String) msg.get("content");
        return null;
    }

    private boolean isKeyMissing() {
        String key = "anthropic".equals(provider) ? anthropicKey : openaiKey;
        return key == null || key.startsWith("skip-") || key.isBlank();
    }
}
