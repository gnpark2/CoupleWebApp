package com.coupleapp.aiservice.dto;
import lombok.Data;
import java.util.List;
@Data
public class DateIdeaRequest {
    private String myCity;
    private String partnerCity;
    private List<String> sharedInterests;
    private String dateType;
    private String context;
}
