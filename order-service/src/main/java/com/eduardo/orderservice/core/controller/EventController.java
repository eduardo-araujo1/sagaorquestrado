package com.eduardo.orderservice.core.controller;

import com.eduardo.orderservice.core.document.Event;
import com.eduardo.orderservice.core.dto.EventFilters;
import com.eduardo.orderservice.core.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/event")
public class EventController {

    private final EventService service;

    @GetMapping()
    public Event findByFilters(@RequestBody EventFilters filters) {
        return service.findByFilters(filters);
    }

    @GetMapping("all")
    public List<Event> findAll() {
        return service.findAll();
    }
}
