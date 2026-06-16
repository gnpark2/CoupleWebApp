package com.coupleapp.calendarservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class CalendarEventResponse {
    private UUID id;
    private UUID coupleId;
    private UUID createdByUserId;
    private String title;
    private String description;
    private LocalDate eventDate;
    private String eventType;
    private boolean recurring;
    private long daysUntil;
    private Instant createdAt;
}
