package com.nineteens.web;

import com.nineteens.common.dto.PageResponse;
import com.nineteens.service.OrderService;
import com.nineteens.web.dto.OrderDtos;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDtos.OrderResponse checkout(@Valid @RequestBody OrderDtos.CheckoutRequest request) {
        return orderService.checkout(request);
    }

    @GetMapping
    public PageResponse<OrderDtos.OrderResponse> history(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return orderService.myOrders(page, size);
    }

    @GetMapping("/{id}")
    public OrderDtos.OrderResponse get(@PathVariable Long id) {
        return orderService.myOrder(id);
    }
}
