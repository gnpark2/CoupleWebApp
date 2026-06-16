package com.coupleapp.feelingservice.service;
import com.coupleapp.common.dto.FeelingSharedEvent;
import com.coupleapp.feelingservice.domain.Feeling;
import com.coupleapp.feelingservice.dto.*;
import com.coupleapp.feelingservice.kafka.FeelingEventProducer;
import com.coupleapp.feelingservice.repository.FeelingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
@Service @RequiredArgsConstructor public class FeelingService{
    private final FeelingRepository repo;
    private final FeelingEventProducer producer;

    @Transactional public FeelingResponse shareFeeling(UUID userId,UUID coupleId,ShareFeelingRequest req){
        if(coupleId==null)throw new IllegalArgumentException("You must be in a couple to share a feeling");
        Feeling f=Feeling.builder().userId(userId).coupleId(coupleId).moodEmoji(req.getMoodEmoji()).moodLabel(req.getMoodLabel()).comment(req.getComment()).build();
        repo.save(f);
        producer.publish(FeelingSharedEvent.builder().coupleId(coupleId).userId(userId).moodEmoji(req.getMoodEmoji()).moodLabel(req.getMoodLabel()).comment(req.getComment()).sharedAt(f.getCreatedAt()).build());
        return toResponse(f);
    }

    @Transactional(readOnly=true) public LatestFeelingsResponse getLatest(UUID userId,UUID coupleId){
        Instant since=Instant.now().minus(24,ChronoUnit.HOURS);
        List<Feeling> mine=repo.findTodayByUser(userId,since);
        FeelingResponse myF=mine.isEmpty()?null:toResponse(mine.get(0));
        FeelingResponse partnerF=null;
        if(coupleId!=null){
            partnerF=repo.findByCoupleIdSince(coupleId,since).stream().filter(x->!x.getUserId().equals(userId)).findFirst().map(this::toResponse).orElse(null);
        }
        return LatestFeelingsResponse.builder().myFeeling(myF).partnerFeeling(partnerF).build();
    }

    @Transactional(readOnly=true) public List<FeelingResponse> getHistory(UUID coupleId,int days){
        return repo.findByCoupleIdSince(coupleId,Instant.now().minus(days,ChronoUnit.DAYS)).stream().map(this::toResponse).toList();
    }

    private FeelingResponse toResponse(Feeling f){return FeelingResponse.builder().id(f.getId()).userId(f.getUserId()).coupleId(f.getCoupleId()).moodEmoji(f.getMoodEmoji()).moodLabel(f.getMoodLabel()).comment(f.getComment()).createdAt(f.getCreatedAt()).build();}
}
