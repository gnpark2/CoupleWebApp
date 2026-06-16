package com.coupleapp.notificationservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "notifications", name = "notification_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID recipientUserId;

    private UUID coupleId;

    // FEELING | DIARY | ANNIVERSARY | CALENDAR | CHARACTER | THINKING_OF_YOU
    @Column(nullable = false, length = 30)
    private String notificationType;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String body;

    // SENT | FAILED | STUB
    @Column(nullable = false, length = 10)
    private String status;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
}
