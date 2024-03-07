package com.eduardo.orchestratorservice.core.consumer;

import com.eduardo.orchestratorservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SagaOrchestratorConsumer {

    private final JsonUtil jsonUtil;
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.start-saga}"

    )
    public void consumeStartSagaEvent(String payload){
        log.info("Recebendo evento de notificação {} desde o início do tópico da saga", payload);
        var event = jsonUtil.toEvent(payload);
        log.info(event.toString());
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.orchestrator}"

    )
    public void consumeOrchestratorEvent(String payload){
        log.info("Recebendo evento de notificação {} do tópico orchestrator", payload);
        var event = jsonUtil.toEvent(payload);
        log.info(event.toString());
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.finish-success}"

    )
    public void consumeFinishSuccessEvent(String payload){
        log.info("Recebendo evento de notificação {} do tópico finish-success", payload);
        var event = jsonUtil.toEvent(payload);
        log.info(event.toString());
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.finish-fail}"

    )
    public void consumeFinishFailEvent(String payload){
        log.info("Recebendo evento de notificação {} do tópico finish-fail", payload);
        var event = jsonUtil.toEvent(payload);
        log.info(event.toString());
    }
}
