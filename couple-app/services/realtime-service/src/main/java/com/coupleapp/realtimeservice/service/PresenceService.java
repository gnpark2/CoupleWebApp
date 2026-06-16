package com.coupleapp.realtimeservice.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;
@Slf4j @Service @RequiredArgsConstructor
public class PresenceService {
    private static final Duration TTL=Duration.ofSeconds(90);
    private final StringRedisTemplate redis;
    private final SimpMessagingTemplate broker;
    public void markOnline(UUID userId,UUID coupleId,String location){
        redis.opsForValue().set("presence:online:"+userId,"1",TTL);
        redis.opsForValue().set("presence:location:"+userId,location,TTL);
        if(coupleId!=null)push(coupleId,userId,true,location);
    }
    public void markOffline(UUID userId,UUID coupleId){
        redis.delete("presence:online:"+userId);
        redis.delete("presence:location:"+userId);
        if(coupleId!=null)push(coupleId,userId,false,null);
    }
    public void heartbeat(UUID userId,UUID coupleId,String location){
        redis.opsForValue().set("presence:online:"+userId,"1",TTL);
        redis.opsForValue().set("presence:location:"+userId,location,TTL);
    }
    public boolean isOnline(UUID userId){return Boolean.TRUE.equals(redis.hasKey("presence:online:"+userId));}
    public String getLocation(UUID userId){return redis.opsForValue().get("presence:location:"+userId);}
    private void push(UUID coupleId,UUID userId,boolean online,String location){
        broker.convertAndSend("/topic/couple."+coupleId+".presence",Map.of("userId",userId.toString(),"online",online,"location",location!=null?location:"","timestamp",Instant.now().toString()));
    }
}
