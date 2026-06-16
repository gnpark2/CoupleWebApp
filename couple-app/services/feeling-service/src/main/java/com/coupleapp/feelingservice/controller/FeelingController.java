package com.coupleapp.feelingservice.controller;
import com.coupleapp.feelingservice.dto.*;
import com.coupleapp.feelingservice.security.AuthenticatedUser;
import com.coupleapp.feelingservice.service.FeelingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/feelings") @RequiredArgsConstructor
public class FeelingController{
    private final FeelingService svc;
    @PostMapping public ResponseEntity<FeelingResponse> share(@AuthenticationPrincipal AuthenticatedUser u,@Valid @RequestBody ShareFeelingRequest req){return ResponseEntity.status(201).body(svc.shareFeeling(u.getUserId(),u.getCoupleId(),req));}
    @GetMapping("/today") public ResponseEntity<LatestFeelingsResponse> today(@AuthenticationPrincipal AuthenticatedUser u){return ResponseEntity.ok(svc.getLatest(u.getUserId(),u.getCoupleId()));}
    @GetMapping("/history") public ResponseEntity<List<FeelingResponse>> history(@AuthenticationPrincipal AuthenticatedUser u,@RequestParam(defaultValue="7") int days){
        if(u.getCoupleId()==null)return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(svc.getHistory(u.getCoupleId(),days));
    }
}
