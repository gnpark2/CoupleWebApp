package com.coupleapp.diaryservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiaryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishDiaryCreated(UUID coupleId, UUID authorId,
                                    String entryType, String title) {
        Map<String, String> event = Map.of(
                "type",      "DIARY_CREATED",
                "coupleId",  coupleId.toString(),
                "authorId",  authorId.toString(),
                "entryType", entryType,
                "title",     title,
                "timestamp", Instant.now().toString()
        );
        String topic = "SETLOG".equals(entryType) ? "setlog.created" : "diary.created";
        kafkaTemplate.send(topic, coupleId.toString(), event);
        log.info("Published {} event for couple {}", entryType, coupleId);
    }
}
