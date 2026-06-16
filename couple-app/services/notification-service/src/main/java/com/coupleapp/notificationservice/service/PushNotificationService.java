package com.coupleapp.notificationservice.service;

import com.coupleapp.notificationservice.domain.NotificationLog;
import com.coupleapp.notificationservice.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final NotificationLogRepository logRepository;

    @Value("${firebase.enabled:false}")
    private boolean firebaseEnabled;

    public void send(UUID recipientUserId, UUID coupleId,
                     String type, String title, String body) {
        String status;
        if (firebaseEnabled) {
            // TODO: wire up Firebase Admin SDK here when credentials are ready
            // FirebaseMessaging.getInstance().send(Message.builder()...);
            log.info("FCM push → user={} type={} title={}", recipientUserId, type, title);
            status = "SENT";
        } else {
            // Stub mode — log the notification but don't actually send
            log.info("[STUB] Push notification → user={} type={} title={} body={}",
                    recipientUserId, type, title, body);
            status = "STUB";
        }

        // Always log to DB regardless of Firebase status
        NotificationLog record = NotificationLog.builder()
                .recipientUserId(recipientUserId)
                .coupleId(coupleId)
                .notificationType(type)
                .title(title)
                .body(body)
                .status(status)
                .build();
        logRepository.save(record);
    }
}
