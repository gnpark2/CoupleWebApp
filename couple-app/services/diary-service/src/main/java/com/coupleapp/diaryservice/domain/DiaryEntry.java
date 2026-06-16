package com.coupleapp.diaryservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(schema = "diary", name = "diary_entries",
       indexes = @Index(name = "idx_diary_couple_date",
                        columnList = "coupleId, entryDate DESC"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID coupleId;

    @Column(nullable = false)
    private UUID authorId;

    // DIARY or SETLOG
    @Column(nullable = false, length = 10)
    private String entryType;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private LocalDate entryDate;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
