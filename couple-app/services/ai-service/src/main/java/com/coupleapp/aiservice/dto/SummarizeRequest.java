package com.coupleapp.aiservice.dto;
import lombok.Data;
import java.util.List;
@Data
public class SummarizeRequest {
    private List<String> entries;
    private String period;
}
