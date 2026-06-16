package com.coupleapp.aiservice.dto;
import lombok.Builder;
import lombok.Data;
import java.util.List;
@Data
@Builder
public class AiResponse {
    private String feature;
    private String result;
    private List<String> items;
    private boolean fromStub;
}
