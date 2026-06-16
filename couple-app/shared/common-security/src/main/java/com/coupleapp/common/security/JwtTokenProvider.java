package com.coupleapp.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class JwtTokenProvider {
    private final Key key;
    private final long accessExpiryMs;
    private final long refreshExpiryMs;

    public JwtTokenProvider(String secret, long accessExpiryMs, long refreshExpiryMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessExpiryMs = accessExpiryMs;
        this.refreshExpiryMs = refreshExpiryMs;
    }

    public String generateAccessToken(UUID userId, UUID coupleId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("coupleId", coupleId != null ? coupleId.toString() : null)
                .claim("type", "access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiryMs))
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public String generateRefreshToken(UUID userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiryMs))
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token) {
        try { parseClaims(token); return true; }
        catch (JwtException | IllegalArgumentException e) { log.warn("Invalid JWT: {}", e.getMessage()); return false; }
    }

    public UUID getUserId(String token) { return UUID.fromString(parseClaims(token).getSubject()); }

    public UUID getCoupleId(String token) {
        String id = parseClaims(token).get("coupleId", String.class);
        return id != null ? UUID.fromString(id) : null;
    }
}
