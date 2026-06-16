package com.coupleapp.coupleservice.dto;
import lombok.*;
import java.time.*;
import java.util.UUID;
@Data @Builder public class CoupleResponse{private UUID coupleId;private UUID userAId;private UUID userBId;private LocalDate anniversaryDate;private String coupleNickname;private Instant pairedAt;private long daysTogether;}
