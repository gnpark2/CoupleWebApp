package com.coupleapp.calendarservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(schema = "calendar", name = "calendar_events",
       indexes = @Index(name = "idx_events_couple_date",
                        columnList = "coupleId, eventDate"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID coupleId;

    @Column(nullable = false)
    private UUID createdByUserId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate eventDate;

    // ANNIVERSARY | MEETUP | MEMORY | REMINDER
    @Column(nullable = false, length = 20)
    private String eventType;

    // For ANNIVERSARY — repeat every year
    private boolean recurring;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
