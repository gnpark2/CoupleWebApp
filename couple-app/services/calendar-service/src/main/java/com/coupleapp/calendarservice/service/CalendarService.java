package com.coupleapp.calendarservice.service;

import com.coupleapp.calendarservice.domain.Anniversary;
import com.coupleapp.calendarservice.domain.CalendarEvent;
import com.coupleapp.calendarservice.dto.*;
import com.coupleapp.calendarservice.kafka.CalendarEventProducer;
import com.coupleapp.calendarservice.repository.AnniversaryRepository;
import com.coupleapp.calendarservice.repository.CalendarEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarEventRepository eventRepository;
    private final AnniversaryRepository   anniversaryRepository;
    private final CalendarEventProducer   eventProducer;

    // ── Events ────────────────────────────────────────────

    @Transactional
    public CalendarEventResponse createEvent(UUID coupleId, UUID userId,
                                              CreateEventRequest req) {
        CalendarEvent event = CalendarEvent.builder()
                .coupleId(coupleId)
                .createdByUserId(userId)
                .title(req.getTitle())
                .description(req.getDescription())
                .eventDate(req.getEventDate())
                .eventType(req.getEventType().toUpperCase())
                .recurring(req.isRecurring())
                .build();
        eventRepository.save(event);
        eventProducer.publishEventCreated(coupleId, req.getTitle(), req.getEventType());
        return toResponse(event);
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> getAllEvents(UUID coupleId) {
        return eventRepository.findByCoupleIdOrderByEventDateAsc(coupleId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> getUpcomingEvents(UUID coupleId) {
        return eventRepository.findUpcoming(coupleId, LocalDate.now())
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> getEventsInRange(UUID coupleId,
                                                         LocalDate from,
                                                         LocalDate to) {
        return eventRepository.findByCoupleIdBetween(coupleId, from, to)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public CalendarEventResponse updateEvent(UUID eventId, UUID coupleId,
                                              UpdateEventRequest req) {
        CalendarEvent event = findEventOrThrow(eventId, coupleId);
        if (req.getTitle() != null)       event.setTitle(req.getTitle());
        if (req.getDescription() != null) event.setDescription(req.getDescription());
        if (req.getEventDate() != null)   event.setEventDate(req.getEventDate());
        if (req.getRecurring() != null)   event.setRecurring(req.getRecurring());
        eventRepository.save(event);
        return toResponse(event);
    }

    @Transactional
    public void deleteEvent(UUID eventId, UUID coupleId) {
        CalendarEvent event = findEventOrThrow(eventId, coupleId);
        eventRepository.delete(event);
    }

    // ── Anniversary ───────────────────────────────────────

    @Transactional
    public AnniversaryResponse setAnniversary(UUID coupleId,
                                               SetAnniversaryRequest req) {
        Anniversary ann = anniversaryRepository.findByCoupleId(coupleId)
                .orElse(Anniversary.builder().coupleId(coupleId).build());
        ann.setStartDate(req.getStartDate());
        anniversaryRepository.save(ann);
        return toAnniversaryResponse(ann);
    }

    @Transactional(readOnly = true)
    public AnniversaryResponse getAnniversary(UUID coupleId) {
        Anniversary ann = anniversaryRepository.findByCoupleId(coupleId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Anniversary not set yet"));
        return toAnniversaryResponse(ann);
    }

    // ── helpers ───────────────────────────────────────────

    private CalendarEvent findEventOrThrow(UUID eventId, UUID coupleId) {
        CalendarEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        if (!event.getCoupleId().equals(coupleId)) {
            throw new IllegalArgumentException("Access denied");
        }
        return event;
    }

    private CalendarEventResponse toResponse(CalendarEvent e) {
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), e.getEventDate());
        return CalendarEventResponse.builder()
                .id(e.getId())
                .coupleId(e.getCoupleId())
                .createdByUserId(e.getCreatedByUserId())
                .title(e.getTitle())
                .description(e.getDescription())
                .eventDate(e.getEventDate())
                .eventType(e.getEventType())
                .recurring(e.isRecurring())
                .daysUntil(daysUntil)
                .createdAt(e.getCreatedAt())
                .build();
    }

    private AnniversaryResponse toAnniversaryResponse(Anniversary ann) {
        LocalDate today = LocalDate.now();
        LocalDate start = ann.getStartDate();
        long daysTogether = ChronoUnit.DAYS.between(start, today);

        // Next anniversary = same month/day, next year if already passed this year
        LocalDate nextAnn = start.withYear(today.getYear());
        if (!nextAnn.isAfter(today)) {
            nextAnn = nextAnn.plusYears(1);
        }
        long daysUntil = ChronoUnit.DAYS.between(today, nextAnn);

        return AnniversaryResponse.builder()
                .startDate(start)
                .daysTogether(daysTogether)
                .nextAnniversary(nextAnn)
                .daysUntilNextAnniversary(daysUntil)
                .build();
    }
}
