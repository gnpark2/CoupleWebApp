package com.coupleapp.diaryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDiaryRequest {

    // DIARY or SETLOG
    @NotBlank
    private String entryType;

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    private String content;

    private LocalDate entryDate;
}
