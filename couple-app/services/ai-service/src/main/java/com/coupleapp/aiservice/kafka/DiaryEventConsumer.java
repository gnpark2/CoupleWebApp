package com.coupleapp.aiservice.kafka;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;
@Slf4j
@Component
@RequiredArgsConstructor
public class DiaryEventConsumer {
    @KafkaListener(topics = "diary.created", groupId = "ai-service")
    public void onDiaryCreated(Map<String, String> event) {
        log.debug("AI service received diary event for couple {}", event.get("coupleId"));
    }
}
