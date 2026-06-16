package com.coupleapp.calendarservice.repository;

import com.coupleapp.calendarservice.domain.Anniversary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AnniversaryRepository extends JpaRepository<Anniversary, UUID> {
    Optional<Anniversary> findByCoupleId(UUID coupleId);
}
