package com.coupleapp.userservice.domain;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.*;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(schema="users",name="user_profiles")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UserProfile {
    @Id private UUID userId;
    @Column(nullable=false,length=30) private String nickname;
    @Column(length=500) private String bio;
    @Column(nullable=false) private String timezone;
    @Column(nullable=false) private String city;
    @Column(length=500) private String profileImageUrl;
    private UUID coupleId;
    @CreationTimestamp @Column(updatable=false) private Instant createdAt;
    @UpdateTimestamp private Instant updatedAt;
}
