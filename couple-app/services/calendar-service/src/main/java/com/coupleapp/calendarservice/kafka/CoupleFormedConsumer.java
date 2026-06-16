package com.coupleapp.calendarservice.kafka;

import com.coupleapp.calendarservice.domain.Anniversary;
import com.coupleapp.calendarservice.repository.AnniversaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoupleFormedConsumer {

    private final AnniversaryRepository anniversaryRepository;

    @KafkaListener(topics = "couple.formed", groupId = "calendar-service")
    public void onCoupleFormed(Map<String, String> event) {
        UUID coupleId = UUID.fromString(event.get("coupleId"));

        // Auto-create anniversary record when couple pairs
        if (!anniversaryRepository.findByCoupleId(coupleId).isPresent()) {
            Anniversary anniversary = Anniversary.builder()
                    .coupleId(coupleId)
                    .startDate(LocalDate.now())
                    .build();
            anniversaryRepository.save(anniversary);
            log.info("Created anniversary for new couple {}", coupleId);
        }
    }
}
