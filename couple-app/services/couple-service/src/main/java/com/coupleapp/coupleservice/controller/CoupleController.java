package com.coupleapp.coupleservice.controller;
import com.coupleapp.coupleservice.dto.*;
import com.coupleapp.coupleservice.security.AuthenticatedUser;
import com.coupleapp.coupleservice.service.CoupleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/couples") @RequiredArgsConstructor
public class CoupleController {
    private final CoupleService svc;
    @PostMapping("/invite") public ResponseEntity<InviteResponse> invite(@AuthenticationPrincipal AuthenticatedUser u){return ResponseEntity.ok(svc.generateInviteCode(u.getUserId()));}
    @PostMapping("/join") public ResponseEntity<CoupleResponse> join(@AuthenticationPrincipal AuthenticatedUser u,@Valid @RequestBody JoinRequest req){return ResponseEntity.status(201).body(svc.joinWithCode(u.getUserId(),req));}
    @GetMapping("/me") public ResponseEntity<CoupleResponse> getMe(@AuthenticationPrincipal AuthenticatedUser u){
        return ResponseEntity.ok(u.getCoupleId()!=null?svc.getCouple(u.getCoupleId()):svc.getCoupleByUser(u.getUserId()));
    }
    @PutMapping("/me") public ResponseEntity<CoupleResponse> updateMe(@AuthenticationPrincipal AuthenticatedUser u,@RequestBody UpdateCoupleRequest req){
        if(u.getCoupleId()==null)return ResponseEntity.status(403).build();
        return ResponseEntity.ok(svc.updateCouple(u.getCoupleId(),req));
    }
}
