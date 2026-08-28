package com.nineteens.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public final class CartDtos {

    private CartDtos() {
    }

    public record AddItemRequest(@NotNull Long productId, @Min(1) int quantity) {
    }

    public record UpdateItemRequest(@Min(1) int quantity) {
    }

    public record CartItemResponse(
            Long id,
            Long productId,
            String productName,
            String imageUrl,
            int quantity,
            int stockQuantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal
    ) {
    }

    public record CartResponse(
            Long id,
            List<CartItemResponse> items,
            int itemCount,
            BigDecimal subtotal,
            BigDecimal discount,
            BigDecimal total
    ) {
    }
}
