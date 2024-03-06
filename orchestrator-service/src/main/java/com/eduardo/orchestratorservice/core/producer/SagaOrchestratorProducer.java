package com.eduardo.orchestratorservice.core.producer;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SagaOrchestratorProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;


    public void sendEvent(String payload, String topic){
        try {
            log.info("Enviando evento para tópico {} com dados {}", topic, payload);
            kafkaTemplate.send(topic, payload);
        }catch (Exception ex){
            log.error("Erro ao tentar enviar dados para o tópico {} com dados {}", topic, payload, ex);
        }
    }
}
