package com.coupleapp.realtimeservice.kafka;
import com.coupleapp.common.dto.FeelingSharedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor
public class FeelingEventConsumer{
    private final SimpMessagingTemplate broker;
    @KafkaListener(topics="feeling.shared",groupId="realtime-service")
    public void onFeeling(FeelingSharedEvent e){broker.convertAndSend("/topic/couple."+e.getCoupleId()+".feeling",e);}
}
