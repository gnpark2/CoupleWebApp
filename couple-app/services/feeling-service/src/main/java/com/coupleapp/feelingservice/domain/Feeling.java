package com.coupleapp.feelingservice.domain;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(schema="feelings",name="feelings",indexes=@Index(name="idx_feelings_couple",columnList="coupleId,createdAt"))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Feeling{
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private UUID coupleId;
    @Column(nullable=false) private UUID userId;
    @Column(nullable=false,length=10) private String moodEmoji;
    @Column(nullable=false,length=50) private String moodLabel;
    @Column(length=200) private String comment;
    @CreationTimestamp @Column(updatable=false) private Instant createdAt;
}
