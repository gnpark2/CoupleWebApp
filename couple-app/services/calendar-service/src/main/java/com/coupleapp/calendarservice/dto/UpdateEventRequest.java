package com.coupleapp.calendarservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateEventRequest {
    @Size(max = 100)
    private String title;
    @Size(max = 500)
    private String description;
    private LocalDate eventDate;
    private Boolean recurring;
}
