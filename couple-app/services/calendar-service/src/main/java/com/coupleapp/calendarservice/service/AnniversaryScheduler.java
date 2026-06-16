package com.coupleapp.calendarservice.service;

import com.coupleapp.calendarservice.kafka.CalendarEventProducer;
import com.coupleapp.calendarservice.repository.AnniversaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnniversaryScheduler {

    private final AnniversaryRepository anniversaryRepository;
    private final CalendarEventProducer eventProducer;

    // Run every day at 09:00 to check for anniversaries
    @Scheduled(cron = "0 0 9 * * *")
    public void checkAnniversaries() {
        LocalDate today = LocalDate.now();
        anniversaryRepository.findAll().forEach(ann -> {
            LocalDate start = ann.getStartDate();
            // Check if today is the anniversary (same month and day)
            if (today.getMonthValue() == start.getMonthValue()
                    && today.getDayOfMonth() == start.getDayOfMonth()) {
                long days = ChronoUnit.DAYS.between(start, today);
                eventProducer.publishAnniversaryTrigger(ann.getCoupleId(), days);
                log.info("Anniversary! Couple {} — {} days together", ann.getCoupleId(), days);
            }
        });
    }
}
