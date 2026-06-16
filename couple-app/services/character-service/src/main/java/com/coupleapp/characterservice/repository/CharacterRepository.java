package com.coupleapp.characterservice.repository;

import com.coupleapp.characterservice.domain.Character;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CharacterRepository extends JpaRepository<Character, UUID> {
    Optional<Character> findByCoupleId(UUID coupleId);
    boolean existsByCoupleId(UUID coupleId);
}
