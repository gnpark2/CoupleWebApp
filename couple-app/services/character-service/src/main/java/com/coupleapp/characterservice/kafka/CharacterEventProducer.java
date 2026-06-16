package com.coupleapp.characterservice.kafka;

import com.coupleapp.common.dto.CharacterXpEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CharacterEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishXpGained(UUID coupleId, UUID userId, String action, int xp) {
        CharacterXpEvent event = CharacterXpEvent.builder()
                .coupleId(coupleId)
                .triggeredByUserId(userId)
                .interactionType(action)
                .xpGained(xp)
                .timestamp(Instant.now())
                .build();
        kafkaTemplate.send("character.xp.gained", coupleId.toString(), event);
        log.info("Published XP event: couple={} action={} xp={}", coupleId, action, xp);
    }
}
