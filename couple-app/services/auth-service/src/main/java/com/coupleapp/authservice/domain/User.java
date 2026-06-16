package com.coupleapp.authservice.domain;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(schema="auth",name="users") @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(unique=true,nullable=false) private String email;
    @Column(nullable=false) private String passwordHash;
    @Column(nullable=false,length=30) private String nickname;
    private UUID coupleId;
    @CreationTimestamp @Column(updatable=false) private Instant createdAt;
}
