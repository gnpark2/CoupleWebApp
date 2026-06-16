package com.coupleapp.userservice.service;
import com.coupleapp.userservice.domain.UserProfile;
import com.coupleapp.userservice.dto.*;
import com.coupleapp.userservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Service @RequiredArgsConstructor public class UserProfileService {
    private final UserProfileRepository repo;
    @Transactional public UserProfileResponse createProfile(UUID userId,String nickname,String timezone,String city){
        UserProfile p=UserProfile.builder().userId(userId).nickname(nickname).timezone(timezone!=null?timezone:"UTC").city(city!=null?city:"Unknown").build();
        repo.save(p); return toResponse(p);
    }
    @Transactional(readOnly=true) public UserProfileResponse getProfile(UUID userId){return toResponse(find(userId));}
    @Transactional public UserProfileResponse updateProfile(UUID userId,UpdateProfileRequest req){
        UserProfile p=find(userId);
        if(req.getNickname()!=null)p.setNickname(req.getNickname());
        if(req.getBio()!=null)p.setBio(req.getBio());
        if(req.getTimezone()!=null)p.setTimezone(req.getTimezone());
        if(req.getCity()!=null)p.setCity(req.getCity());
        if(req.getProfileImageUrl()!=null)p.setProfileImageUrl(req.getProfileImageUrl());
        repo.save(p); return toResponse(p);
    }
    @Transactional public void linkCouple(UUID userId,UUID coupleId){UserProfile p=find(userId);p.setCoupleId(coupleId);repo.save(p);}
    @Transactional(readOnly=true) public UserProfileResponse getPartnerProfile(UUID coupleId,UUID myId){
        return repo.findByCoupleId(coupleId).filter(p->!p.getUserId().equals(myId)).map(this::toResponse).orElseThrow(()->new IllegalArgumentException("Partner not found"));
    }
    private UserProfile find(UUID uid){return repo.findById(uid).orElseThrow(()->new IllegalArgumentException("Profile not found"));}
    private UserProfileResponse toResponse(UserProfile p){return UserProfileResponse.builder().userId(p.getUserId()).nickname(p.getNickname()).bio(p.getBio()).timezone(p.getTimezone()).city(p.getCity()).profileImageUrl(p.getProfileImageUrl()).coupleId(p.getCoupleId()).paired(p.getCoupleId()!=null).build();}
}
