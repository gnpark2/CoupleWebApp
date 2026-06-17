package com.coupleapp.coupleservice.service;

import com.coupleapp.coupleservice.domain.Couple;
import com.coupleapp.coupleservice.dto.*;
import com.coupleapp.coupleservice.kafka.CoupleEventProducer;
import com.coupleapp.coupleservice.repository.CoupleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoupleService {
    private static final String CHARS="ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final CoupleRepository repo;
    private final StringRedisTemplate redis;
    private final CoupleEventProducer producer;

    private void notifyAuthService(UUID userId, UUID coupleId) {
        try {
            java.net.URI uri = java.net.URI.create(
                "http://auth-service:8081/api/auth/internal/set-couple"
                + "?userId=" + userId + "&coupleId=" + coupleId);
            java.net.http.HttpClient.newHttpClient().send(
                java.net.http.HttpRequest.newBuilder(uri).POST(
                    java.net.http.HttpRequest.BodyPublishers.noBody()).build(),
                java.net.http.HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            log.warn("Could not notify auth-service for user {}: {}", userId, e.getMessage());
        }
    }

    public InviteResponse generateInviteCode(UUID userId){
        if(repo.existsByUserAIdOrUserBId(userId,userId))throw new IllegalArgumentException("Already in a couple");
        String code=randomCode(6);
        redis.opsForValue().set("couple:invite:"+code,userId.toString(),java.time.Duration.ofSeconds(86400));
        return new InviteResponse(code,86400);
    }

    @Transactional public CoupleResponse joinWithCode(UUID userBId,JoinRequest req){
        String code=req.getInviteCode().toUpperCase();
        String raw=redis.opsForValue().get("couple:invite:"+code);
        if(raw==null)throw new IllegalArgumentException("Invite code not found or expired");
        UUID userAId=UUID.fromString(raw);
        if(userAId.equals(userBId))throw new IllegalArgumentException("Cannot pair with yourself");
        if(repo.existsByUserAIdOrUserBId(userBId,userBId))throw new IllegalArgumentException("Already in a couple");
        Couple c=Couple.builder().userAId(userAId).userBId(userBId).anniversaryDate(LocalDate.now()).build();
        repo.save(c);
        notifyAuthService(userAId, c.getId());
        notifyAuthService(userBId, c.getId());
        redis.delete("couple:invite:"+code);
        producer.publishCoupleFormed(c.getId(),userAId,userBId);
        return toResponse(c);
    }

    @Transactional(readOnly=true) public CoupleResponse getCouple(UUID coupleId){return toResponse(repo.findById(coupleId).orElseThrow(()->new IllegalArgumentException("Couple not found")));}
    @Transactional(readOnly=true) public CoupleResponse getCoupleByUser(UUID userId){return toResponse(repo.findByUserId(userId).orElseThrow(()->new IllegalArgumentException("Not in a couple yet")));}

    @Transactional public CoupleResponse updateCouple(UUID coupleId,UpdateCoupleRequest req){
        Couple c=repo.findById(coupleId).orElseThrow(()->new IllegalArgumentException("Couple not found"));
        if(req.getAnniversaryDate()!=null)c.setAnniversaryDate(req.getAnniversaryDate());
        if(req.getCoupleNickname()!=null)c.setCoupleNickname(req.getCoupleNickname());
        repo.save(c); return toResponse(c);
    }

    private CoupleResponse toResponse(Couple c){
        long days=c.getPairedAt()!=null?ChronoUnit.DAYS.between(c.getPairedAt(),Instant.now()):0;
        return CoupleResponse.builder().coupleId(c.getId()).userAId(c.getUserAId()).userBId(c.getUserBId()).anniversaryDate(c.getAnniversaryDate()).coupleNickname(c.getCoupleNickname()).pairedAt(c.getPairedAt()).daysTogether(days).build();
    }
    private String randomCode(int n){SecureRandom r=new SecureRandom();StringBuilder sb=new StringBuilder(n);for(int i=0;i<n;i++)sb.append(CHARS.charAt(r.nextInt(CHARS.length())));return sb.toString();}
}
