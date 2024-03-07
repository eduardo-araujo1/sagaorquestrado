package com.eduardo.orderservice.core.service;

import com.eduardo.orderservice.core.document.Event;
import com.eduardo.orderservice.core.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository repository;

    public void notifyEnding(Event event){
        event.setOrderId(event.getOrderId());
        event.setCreatedAt(LocalDateTime.now());
        save(event);
        log.info("Order {} with saga notified! TransactionId {}", event.getOrderId(), event.getTransactionId());
    }

    public Event save(Event event){
        return repository.save(event);
    }
}
