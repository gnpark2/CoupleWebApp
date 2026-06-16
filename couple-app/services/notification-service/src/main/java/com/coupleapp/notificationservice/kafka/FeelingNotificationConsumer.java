package com.coupleapp.notificationservice.kafka;

import com.coupleapp.common.dto.FeelingSharedEvent;
import com.coupleapp.notificationservice.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeelingNotificationConsumer {

    private final PushNotificationService pushService;

    @KafkaListener(topics = "feeling.shared", groupId = "notification-service")
    public void onFeelingShared(FeelingSharedEvent event) {
        log.info("Notifying partner about feeling from couple {}", event.getCoupleId());
        // We notify the partner — in a real system we'd look up the partner's
        // FCM token from user-service. For now we log with coupleId as recipient hint.
        String body = event.getComment() != null ? event.getComment()
                : event.getMoodEmoji() + " " + event.getMoodLabel();
        pushService.send(
                event.getUserId(),      // sender (partner will be looked up in real impl)
                event.getCoupleId(),
                "FEELING",
                "Your partner shared a feeling",
                body
        );
    }
}
