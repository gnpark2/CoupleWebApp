package com.coupleapp.diaryservice.service;

import com.coupleapp.diaryservice.domain.DiaryDocument;
import com.coupleapp.diaryservice.domain.DiaryEntry;
import com.coupleapp.diaryservice.dto.*;
import com.coupleapp.diaryservice.kafka.DiaryEventProducer;
import com.coupleapp.diaryservice.repository.DiaryRepository;
import com.coupleapp.diaryservice.repository.DiarySearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiarySearchRepository searchRepository;
    private final DiaryEventProducer eventProducer;

    @Transactional
    public DiaryResponse createEntry(UUID authorId, UUID coupleId, CreateDiaryRequest req) {
        if (coupleId == null) {
            throw new IllegalArgumentException("You must be in a couple to write a diary entry");
        }
        String type = req.getEntryType().toUpperCase();
        if (!type.equals("DIARY") && !type.equals("SETLOG")) {
            throw new IllegalArgumentException("entryType must be DIARY or SETLOG");
        }

        DiaryEntry entry = DiaryEntry.builder()
                .coupleId(coupleId)
                .authorId(authorId)
                .entryType(type)
                .title(req.getTitle())
                .content(req.getContent())
                .entryDate(req.getEntryDate() != null ? req.getEntryDate() : LocalDate.now())
                .build();
        diaryRepository.save(entry);

        // Index in Elasticsearch for full-text search
        indexToElasticsearch(entry);

        // Notify partner via Kafka
        eventProducer.publishDiaryCreated(coupleId, authorId, type, req.getTitle());

        log.info("Created {} entry for couple {}", type, coupleId);
        return toResponse(entry);
    }

    @Transactional(readOnly = true)
    public List<DiaryResponse> getEntries(UUID coupleId) {
        return diaryRepository.findByCoupleIdOrderByDate(coupleId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DiaryResponse> getEntriesByType(UUID coupleId, String type) {
        return diaryRepository.findByCoupleIdAndType(coupleId, type.toUpperCase())
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DiaryResponse getEntry(UUID entryId, UUID coupleId) {
        DiaryEntry entry = findOrThrow(entryId);
        if (!entry.getCoupleId().equals(coupleId)) {
            throw new IllegalArgumentException("Access denied");
        }
        return toResponse(entry);
    }

    @Transactional
    public DiaryResponse updateEntry(UUID entryId, UUID coupleId,
                                     UUID userId, UpdateDiaryRequest req) {
        DiaryEntry entry = findOrThrow(entryId);
        if (!entry.getCoupleId().equals(coupleId)) {
            throw new IllegalArgumentException("Access denied");
        }
        if (!entry.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("Only the author can edit this entry");
        }
        if (req.getTitle() != null)   entry.setTitle(req.getTitle());
        if (req.getContent() != null) entry.setContent(req.getContent());
        diaryRepository.save(entry);
        indexToElasticsearch(entry);
        return toResponse(entry);
    }

    @Transactional
    public void deleteEntry(UUID entryId, UUID coupleId, UUID userId) {
        DiaryEntry entry = findOrThrow(entryId);
        if (!entry.getCoupleId().equals(coupleId)) {
            throw new IllegalArgumentException("Access denied");
        }
        if (!entry.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("Only the author can delete this entry");
        }
        diaryRepository.delete(entry);
        searchRepository.deleteById(entryId.toString());
    }

    // Full-text search via Elasticsearch
    public List<DiaryResponse> search(UUID coupleId, String query) {
        String cid = coupleId.toString();
        return searchRepository
                .findByCoupleIdAndTitleContainingOrCoupleIdAndContentContaining(
                        cid, query, cid, query)
                .stream()
                .map(doc -> DiaryResponse.builder()
                        .id(UUID.fromString(doc.getId()))
                        .coupleId(UUID.fromString(doc.getCoupleId()))
                        .authorId(UUID.fromString(doc.getAuthorId()))
                        .entryType(doc.getEntryType())
                        .title(doc.getTitle())
                        .content(doc.getContent())
                        .entryDate(doc.getEntryDate())
                        .createdAt(doc.getCreatedAt())
                        .build())
                .toList();
    }

    // ── private helpers ───────────────────────────────────

    private DiaryEntry findOrThrow(UUID id) {
        return diaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entry not found"));
    }

    private void indexToElasticsearch(DiaryEntry entry) {
        DiaryDocument doc = DiaryDocument.builder()
                .id(entry.getId().toString())
                .coupleId(entry.getCoupleId().toString())
                .authorId(entry.getAuthorId().toString())
                .entryType(entry.getEntryType())
                .title(entry.getTitle())
                .content(entry.getContent())
                .entryDate(entry.getEntryDate())
                .createdAt(entry.getCreatedAt())
                .build();
        searchRepository.save(doc);
    }

    private DiaryResponse toResponse(DiaryEntry e) {
        return DiaryResponse.builder()
                .id(e.getId())
                .coupleId(e.getCoupleId())
                .authorId(e.getAuthorId())
                .entryType(e.getEntryType())
                .title(e.getTitle())
                .content(e.getContent())
                .entryDate(e.getEntryDate())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
