package com.coupleapp.characterservice.controller;

import com.coupleapp.characterservice.dto.*;
import com.coupleapp.characterservice.security.AuthenticatedUser;
import com.coupleapp.characterservice.service.CharacterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {

    private final CharacterService characterService;

    // Create Nabi for this couple (called once after pairing)
    @PostMapping
    public ResponseEntity<CharacterResponse> createCharacter(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateCharacterRequest req) {
        if (user.getCoupleId() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(characterService.createCharacter(user.getCoupleId(), req));
    }

    // Get Nabi's current state
    @GetMapping
    public ResponseEntity<CharacterResponse> getCharacter(
            @AuthenticationPrincipal AuthenticatedUser user) {
        if (user.getCoupleId() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(characterService.getCharacter(user.getCoupleId()));
    }

    // Interact: feed / play / pat / rest
    @PostMapping("/interact")
    public ResponseEntity<CharacterResponse> interact(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody InteractRequest req) {
        if (user.getCoupleId() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(
                characterService.interact(user.getCoupleId(), user.getUserId(), req));
    }

    // Rename or change avatar
    @PutMapping
    public ResponseEntity<CharacterResponse> updateCharacter(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody UpdateCharacterRequest req) {
        if (user.getCoupleId() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(
                characterService.updateCharacter(user.getCoupleId(), req));
    }
}
