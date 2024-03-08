package com.eduardo.orderservice.core.controller;

import com.eduardo.orderservice.core.document.Order;
import com.eduardo.orderservice.core.dto.OrderRequest;
import com.eduardo.orderservice.core.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private OrderService orderService;

    @PostMapping
    public Order create(@RequestBody OrderRequest order) {
        return orderService.createOrder(order);
    }
}
