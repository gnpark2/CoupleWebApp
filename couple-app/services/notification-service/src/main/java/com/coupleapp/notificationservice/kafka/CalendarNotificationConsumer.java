package com.coupleapp.notificationservice.kafka;

import com.coupleapp.notificationservice.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalendarNotificationConsumer {

    private final PushNotificationService pushService;

    @KafkaListener(topics = "calendar.event.created", groupId = "notification-service")
    public void onCalendarEvent(Map<String, String> event) {
        UUID coupleId = UUID.fromString(event.get("coupleId"));
        String title  = event.getOrDefault("title", "New event");

        pushService.send(
                coupleId,
                coupleId,
                "CALENDAR",
                "New calendar event",
                title
        );
    }
}
