package com.eduardo.orderservice.core.consumer;

import com.eduardo.orderservice.core.service.EventService;
import com.eduardo.orderservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class EventConsumer {

    private final JsonUtil jsonUtil;
    private final EventService service;
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.notify-ending}"

    )
    public void consumeNotifyEndingEvent(String payload){
        log.info("Recebendo evento de notificação de encerramento {} do tópico de encerramento de notificação", payload);
        var event = jsonUtil.toEvent(payload);
        service.notifyEnding(event);
    }
}
