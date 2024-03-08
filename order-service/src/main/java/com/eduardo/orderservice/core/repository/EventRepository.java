package com.eduardo.orderservice.core.repository;

import com.eduardo.orderservice.core.document.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends MongoRepository<Event, String> {

    List<Event> findAllByOrderByCreatedAtDesc();

    Optional<Event> findTop1ByOrderIdOrderByCreatedAtDesc(String orderId);

    Optional<Event> findTop1ByTransactionIdOrderByCreatedAtDesc(String transactionId);
}