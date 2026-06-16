package com.coupleapp.coupleservice.kafka;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.UUID;
@Slf4j @Component @RequiredArgsConstructor
public class CoupleEventProducer{
    private final KafkaTemplate<String,Object> kafkaTemplate;
    public void publishCoupleFormed(UUID coupleId,UUID userAId,UUID userBId){
        Map<String,String> e=Map.of("type","COUPLE_FORMED","coupleId",coupleId.toString(),"userAId",userAId.toString(),"userBId",userBId.toString());
        kafkaTemplate.send("couple.formed",coupleId.toString(),e);
        log.info("Published COUPLE_FORMED for couple {}",coupleId);
    }
}
