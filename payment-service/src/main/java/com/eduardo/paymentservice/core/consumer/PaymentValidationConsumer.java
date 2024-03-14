package com.eduardo.paymentservice.core.consumer;

import com.eduardo.paymentservice.core.service.PaymentService;
import com.eduardo.paymentservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentValidationConsumer {

    private final JsonUtil jsonUtil;
    private final PaymentService paymentService;
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.payment-success}"

    )
    public void consumeSuccessEvent(String payload){
        log.info("Recebendo evento de sucesso {} do topico payment-success", payload);
        var event = jsonUtil.toEvent(payload);
        paymentService.realizePayment(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.payment-fail}"

    )
    public void consumeFailEvent(String payload){
        log.info("Recebendo rollback event {} do topico payment-fail ", payload);
        var event = jsonUtil.toEvent(payload);
        paymentService.realizeRefund(event);
    }
}
