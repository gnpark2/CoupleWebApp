package com.coupleapp.calendarservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateEventRequest {

    @NotBlank
    @Size(max = 100)
    private String title;

    @Size(max = 500)
    private String description;

    @NotNull
    private LocalDate eventDate;

    // ANNIVERSARY | MEETUP | MEMORY | REMINDER
    @NotBlank
    private String eventType;

    private boolean recurring;
}
