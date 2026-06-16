package com.coupleapp.userservice.controller;
import com.coupleapp.userservice.dto.*;
import com.coupleapp.userservice.security.AuthenticatedUser;
import com.coupleapp.userservice.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @RequestMapping("/api/users") @RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService svc;
    @GetMapping("/me") public ResponseEntity<UserProfileResponse> getMe(@AuthenticationPrincipal AuthenticatedUser u){return ResponseEntity.ok(svc.getProfile(u.getUserId()));}
    @PutMapping("/me") public ResponseEntity<UserProfileResponse> updateMe(@AuthenticationPrincipal AuthenticatedUser u,@Valid @RequestBody UpdateProfileRequest req){return ResponseEntity.ok(svc.updateProfile(u.getUserId(),req));}
    @GetMapping("/partner") public ResponseEntity<UserProfileResponse> getPartner(@AuthenticationPrincipal AuthenticatedUser u){
        if(u.getCoupleId()==null)return ResponseEntity.notFound().build();
        return ResponseEntity.ok(svc.getPartnerProfile(u.getCoupleId(),u.getUserId()));
    }
    @PostMapping("/internal/link-couple") public ResponseEntity<Void> linkCouple(@RequestParam String userId,@RequestParam String coupleId){
        svc.linkCouple(UUID.fromString(userId),UUID.fromString(coupleId));return ResponseEntity.ok().build();
    }
}
