package com.eduardo.orderservice.core.repository;

import com.eduardo.orderservice.core.document.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<Event, String> {
}
