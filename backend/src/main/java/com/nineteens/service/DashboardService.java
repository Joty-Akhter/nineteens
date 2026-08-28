package com.nineteens.service;

import com.nineteens.domain.order.OrderRepository;
import com.nineteens.domain.order.OrderStatus;
import com.nineteens.domain.product.ProductRepository;
import com.nineteens.domain.user.UserRepository;
import com.nineteens.web.dto.DashboardStats;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public DashboardService(
            UserRepository userRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public DashboardStats stats() {
        BigDecimal revenue = orderRepository.sumNonCancelledTotals();
        return new DashboardStats(
                userRepository.count(),
                orderRepository.count(),
                orderRepository.countByStatus(OrderStatus.PENDING),
                productRepository.count(),
                revenue == null ? BigDecimal.ZERO : revenue);
    }
}
