package com.coupleapp.feelingservice.kafka;
import com.coupleapp.common.dto.FeelingSharedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor
public class FeelingEventProducer{
    private final KafkaTemplate<String,Object> kafkaTemplate;
    public void publish(FeelingSharedEvent e){kafkaTemplate.send("feeling.shared",e.getCoupleId().toString(),e);}
}
