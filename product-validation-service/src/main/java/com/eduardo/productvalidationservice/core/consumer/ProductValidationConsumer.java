package com.eduardo.productvalidationservice.core.consumer;

import com.eduardo.productvalidationservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ProductValidationConsumer {

    private final JsonUtil jsonUtil;
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.product-validation-success}"

    )
    public void consumeSuccessEvent(String payload){
        log.info("Recebendo evento de sucesso {} do topico product-validation-success  ", payload);
        var event = jsonUtil.toEvent(payload);
        log.info(event.toString());
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.product-validation-fail}"

    )
    public void consumeFailEvent(String payload){
        log.info("Recebendo rollback event {} do topico product-validation-fail", payload);
        var event = jsonUtil.toEvent(payload);
        log.info(event.toString());
    }
}
