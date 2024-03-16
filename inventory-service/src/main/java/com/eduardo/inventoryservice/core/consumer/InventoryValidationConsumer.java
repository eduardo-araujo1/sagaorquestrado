package com.eduardo.inventoryservice.core.consumer;

import com.eduardo.inventoryservice.core.service.InventoryService;
import com.eduardo.inventoryservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class InventoryValidationConsumer {

    private final JsonUtil jsonUtil;
    private final InventoryService inventoryService;
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.inventory-success}"

    )
    public void consumeSuccessEvent(String payload){
        log.info("Recebendo evento de sucesso {} do topico inventory-success", payload);
        var event = jsonUtil.toEvent(payload);
        inventoryService.updateInventory(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.inventory-fail}"

    )
    public void consumeFailEvent(String payload){
        log.info("Recebendo rollback event {} do topico inventory-fail", payload);
        var event = jsonUtil.toEvent(payload);
        inventoryService.rollbackInventory(event);
    }
}
