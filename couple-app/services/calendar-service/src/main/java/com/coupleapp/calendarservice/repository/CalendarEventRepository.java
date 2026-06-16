package com.coupleapp.calendarservice.repository;

import com.coupleapp.calendarservice.domain.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, UUID> {

    List<CalendarEvent> findByCoupleIdOrderByEventDateAsc(UUID coupleId);

    @Query("SELECT e FROM CalendarEvent e WHERE e.coupleId = :coupleId " +
           "AND e.eventDate BETWEEN :from AND :to ORDER BY e.eventDate ASC")
    List<CalendarEvent> findByCoupleIdBetween(UUID coupleId, LocalDate from, LocalDate to);

    @Query("SELECT e FROM CalendarEvent e WHERE e.coupleId = :coupleId " +
           "AND e.eventDate >= :today ORDER BY e.eventDate ASC")
    List<CalendarEvent> findUpcoming(UUID coupleId, LocalDate today);
}
