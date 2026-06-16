package com.coupleapp.diaryservice.controller;

import com.coupleapp.diaryservice.dto.*;
import com.coupleapp.diaryservice.security.AuthenticatedUser;
import com.coupleapp.diaryservice.service.DiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    // Create a diary entry or setlog
    @PostMapping
    public ResponseEntity<DiaryResponse> create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateDiaryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(diaryService.createEntry(
                        user.getUserId(), user.getCoupleId(), req));
    }

    // List all entries for the couple
    @GetMapping
    public ResponseEntity<List<DiaryResponse>> list(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) String type) {
        if (user.getCoupleId() == null) return ResponseEntity.ok(List.of());
        if (type != null) {
            return ResponseEntity.ok(
                    diaryService.getEntriesByType(user.getCoupleId(), type));
        }
        return ResponseEntity.ok(diaryService.getEntries(user.getCoupleId()));
    }

    // Get a single entry
    @GetMapping("/{id}")
    public ResponseEntity<DiaryResponse> get(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                diaryService.getEntry(id, user.getCoupleId()));
    }

    // Update an entry
    @PutMapping("/{id}")
    public ResponseEntity<DiaryResponse> update(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDiaryRequest req) {
        return ResponseEntity.ok(
                diaryService.updateEntry(id, user.getCoupleId(), user.getUserId(), req));
    }

    // Delete an entry
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id) {
        diaryService.deleteEntry(id, user.getCoupleId(), user.getUserId());
        return ResponseEntity.noContent().build();
    }

    // Full-text search via Elasticsearch
    @GetMapping("/search")
    public ResponseEntity<List<DiaryResponse>> search(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam String q) {
        if (user.getCoupleId() == null) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(
                diaryService.search(user.getCoupleId(), q));
    }
}
