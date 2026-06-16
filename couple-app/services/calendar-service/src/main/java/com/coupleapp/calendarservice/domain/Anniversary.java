package com.coupleapp.calendarservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(schema = "calendar", name = "anniversaries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Anniversary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID coupleId;

    // The date they first got together
    @Column(nullable = false)
    private LocalDate startDate;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
}
