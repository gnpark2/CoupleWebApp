package com.coupleapp.diaryservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateDiaryRequest {
    @Size(max = 100)
    private String title;
    private String content;
}
