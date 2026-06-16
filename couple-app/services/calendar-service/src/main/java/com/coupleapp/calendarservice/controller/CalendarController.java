package com.coupleapp.calendarservice.controller;

import com.coupleapp.calendarservice.dto.*;
import com.coupleapp.calendarservice.security.AuthenticatedUser;
import com.coupleapp.calendarservice.service.CalendarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    // ── Events ────────────────────────────────────────────

    @PostMapping("/events")
    public ResponseEntity<CalendarEventResponse> createEvent(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateEventRequest req) {
        if (user.getCoupleId() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(calendarService.createEvent(
                        user.getCoupleId(), user.getUserId(), req));
    }

    @GetMapping("/events")
    public ResponseEntity<List<CalendarEventResponse>> listEvents(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (user.getCoupleId() == null) return ResponseEntity.ok(List.of());
        if (from != null && to != null) {
            return ResponseEntity.ok(
                    calendarService.getEventsInRange(user.getCoupleId(), from, to));
        }
        return ResponseEntity.ok(calendarService.getAllEvents(user.getCoupleId()));
    }

    @GetMapping("/events/upcoming")
    public ResponseEntity<List<CalendarEventResponse>> upcoming(
            @AuthenticationPrincipal AuthenticatedUser user) {
        if (user.getCoupleId() == null) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(
                calendarService.getUpcomingEvents(user.getCoupleId()));
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<CalendarEventResponse> updateEvent(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEventRequest req) {
        return ResponseEntity.ok(
                calendarService.updateEvent(id, user.getCoupleId(), req));
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id) {
        calendarService.deleteEvent(id, user.getCoupleId());
        return ResponseEntity.noContent().build();
    }

    // ── Anniversary ───────────────────────────────────────

    @GetMapping("/anniversary")
    public ResponseEntity<AnniversaryResponse> getAnniversary(
            @AuthenticationPrincipal AuthenticatedUser user) {
        if (user.getCoupleId() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(
                calendarService.getAnniversary(user.getCoupleId()));
    }

    @PutMapping("/anniversary")
    public ResponseEntity<AnniversaryResponse> setAnniversary(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody SetAnniversaryRequest req) {
        if (user.getCoupleId() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(
                calendarService.setAnniversary(user.getCoupleId(), req));
    }
}
