package com.coupleapp.coupleservice.domain;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.*;
import java.util.UUID;
@Entity @Table(schema="couples",name="couples")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Couple {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private UUID userAId;
    @Column(nullable=false) private UUID userBId;
    private LocalDate anniversaryDate;
    @Column(length=50) private String coupleNickname;
    @CreationTimestamp @Column(updatable=false) private Instant pairedAt;
}
