package com.coupleapp.diaryservice.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Document(indexName = "diary_entries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryDocument {

    @Id
    private String id;

    private String coupleId;
    private String authorId;
    private String entryType;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;

    private LocalDate entryDate;
    private Instant createdAt;
}
