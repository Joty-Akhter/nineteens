package com.nineteens.web.admin;

import com.nineteens.common.dto.PageResponse;
import com.nineteens.service.OrderService;
import com.nineteens.web.dto.OrderDtos;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public PageResponse<OrderDtos.OrderResponse> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size) {
        return orderService.allOrders(page, size);
    }

    @GetMapping("/{id}")
    public OrderDtos.OrderResponse get(@PathVariable Long id) {
        return orderService.getAdmin(id);
    }

    @PutMapping("/{id}/status")
    public OrderDtos.OrderResponse updateStatus(
            @PathVariable Long id, @Valid @RequestBody OrderDtos.UpdateOrderStatusRequest request) {
        return orderService.updateStatus(id, request.status());
    }
}
