package com.coupleapp.calendarservice.kafka;

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
public class CalendarEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishEventCreated(UUID coupleId, String title, String eventType) {
        Map<String, String> payload = Map.of(
                "type",      "CALENDAR_EVENT_CREATED",
                "coupleId",  coupleId.toString(),
                "title",     title,
                "eventType", eventType,
                "timestamp", Instant.now().toString()
        );
        kafkaTemplate.send("calendar.event.created", coupleId.toString(), payload);
        log.info("Published calendar event for couple {}", coupleId);
    }

    public void publishAnniversaryTrigger(UUID coupleId, long daysTogether) {
        Map<String, String> payload = Map.of(
                "type",         "ANNIVERSARY_TRIGGER",
                "coupleId",     coupleId.toString(),
                "daysTogether", String.valueOf(daysTogether),
                "timestamp",    Instant.now().toString()
        );
        kafkaTemplate.send("anniversary.trigger", coupleId.toString(), payload);
        log.info("Published anniversary trigger for couple {} ({} days)", coupleId, daysTogether);
    }
}
