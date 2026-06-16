package com.coupleapp.realtimeservice.kafka;
import com.coupleapp.common.dto.CharacterXpEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor
public class CharacterEventConsumer{
    private final SimpMessagingTemplate broker;
    @KafkaListener(topics="character.xp.gained",groupId="realtime-service")
    public void onXp(CharacterXpEvent e){broker.convertAndSend("/topic/couple."+e.getCoupleId()+".character",e);}
}
