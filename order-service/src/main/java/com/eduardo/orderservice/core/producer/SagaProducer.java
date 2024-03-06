package com.eduardo.orderservice.core.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.start-saga}")
    private String startSagaTopic;

    public void sendEvent(String payload){
        try {
            log.info("Enviando evento para tópico {} com dados {}", startSagaTopic, payload);
            kafkaTemplate.send(startSagaTopic, payload);
        }catch (Exception ex){
            log.error("Erro ao tentar enviar dados para o tópico {} com dados {}", startSagaTopic, payload, ex);
        }
    }
}
