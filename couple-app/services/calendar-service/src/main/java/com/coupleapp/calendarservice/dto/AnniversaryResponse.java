package com.coupleapp.calendarservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AnniversaryResponse {
    private LocalDate startDate;
    private long daysTogether;
    private LocalDate nextAnniversary;
    private long daysUntilNextAnniversary;
}
