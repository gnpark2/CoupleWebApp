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
public class DiaryNotificationConsumer {

    private final PushNotificationService pushService;

    @KafkaListener(topics = {"diary.created", "setlog.created"},
                   groupId = "notification-service")
    public void onDiaryCreated(Map<String, String> event) {
        UUID coupleId  = UUID.fromString(event.get("coupleId"));
        String type    = event.getOrDefault("entryType", "DIARY");
        String title   = event.getOrDefault("title", "New entry");

        pushService.send(
                UUID.fromString(event.get("authorId")),
                coupleId,
                type,
                "DIARY".equals(type) ? "New diary entry" : "New setlog",
                title
        );
    }
}
