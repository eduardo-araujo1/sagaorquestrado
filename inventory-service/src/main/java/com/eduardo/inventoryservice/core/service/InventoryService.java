package com.eduardo.inventoryservice.core.service;

import com.eduardo.inventoryservice.config.exception.ValidationException;
import com.eduardo.inventoryservice.core.dto.Event;
import com.eduardo.inventoryservice.core.dto.History;
import com.eduardo.inventoryservice.core.dto.Order;
import com.eduardo.inventoryservice.core.dto.OrderProducts;
import com.eduardo.inventoryservice.core.model.Inventory;
import com.eduardo.inventoryservice.core.model.OrderInventory;
import com.eduardo.inventoryservice.core.producer.KafkaProducer;
import com.eduardo.inventoryservice.core.repository.InventoryRepository;
import com.eduardo.inventoryservice.core.repository.OrderInventoryRepository;
import com.eduardo.inventoryservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.eduardo.inventoryservice.core.enums.ESagaStatus.SUCCESS;

@Slf4j
@Service
@AllArgsConstructor
public class InventoryService {

    private static final String CURRENT_SOURCE = "INVENTORY_SERVICE";

    private final JsonUtil jsonUtil;
    private final KafkaProducer producer;
    private final InventoryRepository inventoryRepository;
    private final OrderInventoryRepository orderInventoryRepository;

    public void updateInventory(Event event){
        try {
            checkCurrentValidation(event);
            createOrderInventory(event);
            handleSuccess(event);

        }catch (Exception ex) {
            log.error("Error trying to update inventory: ", ex);
        }
        producer.sendEvent(jsonUtil.toJson(event));
    }

    private void checkCurrentValidation(Event event) {
        if (orderInventoryRepository.existsByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())) {
            throw new ValidationException("There's another transactionId for this validation.");
        }
    }

    private void createOrderInventory(Event event){
        event
                .getPayload()
                .getProducts()
                .forEach(product -> {
                    var inventory = findInventoryByProductCode(product.getProduct().getCode());
                    var orderInventory = createOrderInventory(event, product, inventory);
                    orderInventoryRepository.save(orderInventory);
                });
    }

    private OrderInventory createOrderInventory(Event event, OrderProducts product, Inventory inventory){
        return OrderInventory
                .builder()
                .inventory(inventory)
                .oldQuantity(inventory.getAvailable())
                .orderQuantity(product.getQuantity())
                .newQuantity(inventory.getAvailable() - product.getQuantity())
                .orderId(event.getPayload().getId())
                .transactionId(event.getTransactionId())
                .build();
    }

    private void updateInventory(Order order){
        order
                .getProducts()
                .forEach(product -> {
                    var inventory = findInventoryByProductCode(product.getProduct().getCode());
                    checkInventory(inventory.getAvailable(), product.getQuantity());
                    inventory.setAvailable(inventory.getAvailable() - product.getQuantity());
                    inventoryRepository.save(inventory);
                });
    }

    private void checkInventory(int available, int orderQuantity){
        if (orderQuantity > available){
            throw new ValidationException("Product is out of stock!");
        }
    }

    private void handleSuccess(Event event) {
        event.setStatus(SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Inventory updated successfully");
    }

    private void addHistory(Event event, String message) {
        var history = History
                .builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addToHistory(history);
    }

    private Inventory findInventoryByProductCode(String productCode){
        return inventoryRepository
                .findByProductCode(productCode)
                .orElseThrow(() -> new ValidationException("Inventory not found by informerd product."));
    }
}
