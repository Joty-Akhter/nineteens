package com.nineteens.web.dto;

import java.math.BigDecimal;

public record DashboardStats(
        long totalUsers,
        long totalOrders,
        long pendingOrders,
        long totalProducts,
        BigDecimal totalRevenue
) {
}
