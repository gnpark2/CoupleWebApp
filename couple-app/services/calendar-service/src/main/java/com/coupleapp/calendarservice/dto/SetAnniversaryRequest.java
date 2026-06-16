package com.coupleapp.calendarservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SetAnniversaryRequest {
    @NotNull
    private LocalDate startDate;
}
