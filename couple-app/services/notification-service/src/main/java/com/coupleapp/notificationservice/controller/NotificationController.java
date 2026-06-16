package com.coupleapp.notificationservice.controller;

import com.coupleapp.notificationservice.domain.NotificationLog;
import com.coupleapp.notificationservice.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationLogRepository logRepository;

    // Get notification history for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationLog>> getUserNotifications(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(
                logRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId));
    }
}
