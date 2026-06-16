package com.coupleapp.characterservice.service;

import com.coupleapp.characterservice.domain.Character;
import com.coupleapp.characterservice.dto.*;
import com.coupleapp.characterservice.kafka.CharacterEventProducer;
import com.coupleapp.characterservice.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterService {

    // XP gained per action type
    private static final Map<String, Integer> XP_MAP = Map.of(
            "feed", 15,
            "play", 20,
            "pat",  10,
            "rest", 5
    );

    private final CharacterRepository characterRepository;
    private final CharacterEventProducer eventProducer;

    @Transactional
    public CharacterResponse createCharacter(UUID coupleId, CreateCharacterRequest req) {
        if (characterRepository.existsByCoupleId(coupleId)) {
            throw new IllegalArgumentException("Character already exists for this couple");
        }
        Character character = Character.builder()
                .coupleId(coupleId)
                .name(req.getName())
                .xp(0)
                .level(1)
                .happiness(80)
                .hunger(60)
                .energy(70)
                .avatarEmoji(req.getAvatarEmoji() != null ? req.getAvatarEmoji() : "🐱")
                .theme(req.getTheme() != null ? req.getTheme() : "default")
                .build();
        characterRepository.save(character);
        log.info("Created character {} for couple {}", req.getName(), coupleId);
        return toResponse(character);
    }

    @Transactional(readOnly = true)
    public CharacterResponse getCharacter(UUID coupleId) {
        Character character = findByCoupleOrThrow(coupleId);
        applyDecayIfNeeded(character);
        return toResponse(character);
    }

    @Transactional
    public CharacterResponse interact(UUID coupleId, UUID userId, InteractRequest req) {
        String action = req.getAction().toLowerCase();
        if (!XP_MAP.containsKey(action)) {
            throw new IllegalArgumentException("Unknown action: " + action + ". Use: feed, play, pat, rest");
        }

        Character character = findByCoupleOrThrow(coupleId);

        switch (action) {
            case "feed" -> character.feed();
            case "play" -> character.play();
            case "pat"  -> character.pat();
            case "rest" -> character.rest();
        }

        int xpGained = XP_MAP.get(action);
        character.applyXp(xpGained);
        characterRepository.save(character);

        // Notify partner in real-time via Kafka -> realtime-service
        eventProducer.publishXpGained(coupleId, userId, action, xpGained);

        log.info("Couple {} interacted: {} (+{}xp)", coupleId, action, xpGained);
        return toResponse(character);
    }

    @Transactional
    public CharacterResponse updateCharacter(UUID coupleId, UpdateCharacterRequest req) {
        Character character = findByCoupleOrThrow(coupleId);
        if (req.getName() != null)       character.setName(req.getName());
        if (req.getAvatarEmoji() != null) character.setAvatarEmoji(req.getAvatarEmoji());
        if (req.getTheme() != null)       character.setTheme(req.getTheme());
        characterRepository.save(character);
        return toResponse(character);
    }

    // ── private helpers ───────────────────────────────────

    private Character findByCoupleOrThrow(UUID coupleId) {
        return characterRepository.findByCoupleId(coupleId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No character found for this couple. Create one first."));
    }

    private void applyDecayIfNeeded(Character character) {
        if (character.getUpdatedAt() == null) return;
        long hours = ChronoUnit.HOURS.between(character.getUpdatedAt(), Instant.now());
        if (hours > 0) {
            character.applyDecay(hours);
            characterRepository.save(character);
        }
    }

    private CharacterResponse toResponse(Character c) {
        int xpToNext = 1000 - (c.getXp() % 1000);
        String status = buildStatusMessage(c);
        return CharacterResponse.builder()
                .id(c.getId())
                .coupleId(c.getCoupleId())
                .name(c.getName())
                .xp(c.getXp())
                .level(c.getLevel())
                .xpToNextLevel(xpToNext)
                .happiness(c.getHappiness())
                .hunger(c.getHunger())
                .energy(c.getEnergy())
                .avatarEmoji(c.getAvatarEmoji())
                .theme(c.getTheme())
                .statusMessage(status)
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private String buildStatusMessage(Character c) {
        if (c.getHunger() < 20)   return "I'm so hungry... feed me!";
        if (c.getEnergy() < 20)   return "I need to rest...";
        if (c.getHappiness() < 30) return "I miss you both...";
        if (c.getHappiness() > 80) return "I'm so happy today!";
        return "I'm doing well!";
    }
}
