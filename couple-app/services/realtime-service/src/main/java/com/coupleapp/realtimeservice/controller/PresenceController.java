package com.coupleapp.realtimeservice.controller;
import com.coupleapp.realtimeservice.service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/presence") @RequiredArgsConstructor
public class PresenceController{
    private final PresenceService svc;
    @GetMapping("/{userId}") public ResponseEntity<Map<String,Object>> get(@PathVariable UUID userId){
        return ResponseEntity.ok(Map.of("userId",userId.toString(),"online",svc.isOnline(userId),"location",svc.getLocation(userId)!=null?svc.getLocation(userId):""));
    }
}
