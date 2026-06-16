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
public class AnniversaryNotificationConsumer {

    private final PushNotificationService pushService;

    @KafkaListener(topics = "anniversary.trigger", groupId = "notification-service")
    public void onAnniversary(Map<String, String> event) {
        UUID coupleId  = UUID.fromString(event.get("coupleId"));
        String days    = event.getOrDefault("daysTogether", "?");

        pushService.send(
                coupleId, // in real impl, send to both users
                coupleId,
                "ANNIVERSARY",
                "Happy Anniversary!",
                "You've been together for " + days + " days. Celebrate today!"
        );
    }
}
