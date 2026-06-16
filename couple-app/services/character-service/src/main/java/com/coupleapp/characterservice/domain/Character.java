package com.coupleapp.characterservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "characters", name = "characters")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // One character per couple
    @Column(nullable = false, unique = true)
    private UUID coupleId;

    @Column(nullable = false, length = 30)
    private String name;

    // XP and level
    @Column(nullable = false)
    private int xp;

    @Column(nullable = false)
    private int level;

    // Stats 0-100
    @Column(nullable = false)
    private int happiness;

    @Column(nullable = false)
    private int hunger;

    @Column(nullable = false)
    private int energy;

    // Cosmetics
    @Column(length = 50)
    private String avatarEmoji;

    @Column(length = 50)
    private String theme;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    // ── business helpers ──────────────────────────────────

    public void applyXp(int amount) {
        this.xp += amount;
        // Level up every 1000 XP
        this.level = (this.xp / 1000) + 1;
    }

    public void feed() {
        this.hunger = Math.min(100, this.hunger + 25);
        this.happiness = Math.min(100, this.happiness + 5);
    }

    public void play() {
        this.happiness = Math.min(100, this.happiness + 20);
        this.energy = Math.max(0, this.energy - 15);
        this.hunger = Math.max(0, this.hunger - 10);
    }

    public void pat() {
        this.happiness = Math.min(100, this.happiness + 10);
    }

    public void rest() {
        this.energy = Math.min(100, this.energy + 30);
        this.hunger = Math.max(0, this.hunger - 5);
    }

    // Decay stats over time (called on read)
    public void applyDecay(long hoursSinceUpdate) {
        int decay = (int) Math.min(hoursSinceUpdate * 2, 40);
        this.hunger = Math.max(0, this.hunger - decay);
        this.energy = Math.max(0, this.energy - (int) Math.min(hoursSinceUpdate, 20));
        this.happiness = Math.max(0, this.happiness - (int) Math.min(hoursSinceUpdate, 10));
    }
}
