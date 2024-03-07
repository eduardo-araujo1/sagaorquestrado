package com.eduardo.orderservice.core.service;

import com.eduardo.orderservice.core.document.Event;
import com.eduardo.orderservice.core.document.Order;
import com.eduardo.orderservice.core.dto.OrderRequest;
import com.eduardo.orderservice.core.producer.SagaProducer;
import com.eduardo.orderservice.core.repository.OrderRepository;
import com.eduardo.orderservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    private static final String TRANSACTIONAL_ID_PATTERN = "%s_%s";

    private final JsonUtil jsonUtil;
    private final SagaProducer sagaProducer;
    private final EventService eventService;
    private final OrderRepository repository;

    public Order createOrder(OrderRequest request){
        var order = Order
                .builder()
                .products(request.getProducts())
                .createdAt(LocalDateTime.now())
                .transactionId(
                        String.format(TRANSACTIONAL_ID_PATTERN, Instant.now().toEpochMilli(), UUID.randomUUID())
                )
                .build();
        repository.save(order);
        sagaProducer.sendEvent(jsonUtil.toJson(createPayload(order)));
        return order;
    }

    private Event createPayload(Order order){
        var event = Event.builder()
                .orderId(order.getId())
                .transactionId(order.getTransactionId())
                .payload(order)
                .createdAt(LocalDateTime.now())
                .build();
        eventService.save(event);
        return event;
    }
}
