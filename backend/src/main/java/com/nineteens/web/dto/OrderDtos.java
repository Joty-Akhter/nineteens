package com.nineteens.web.dto;

import com.nineteens.domain.order.OrderStatus;
import com.nineteens.domain.order.PaymentProvider;
import com.nineteens.domain.order.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class OrderDtos {

    private OrderDtos() {
    }

    public record CheckoutRequest(
            Long addressId,
            @NotBlank @Size(max = 150) String recipientName,
            @NotBlank @Size(max = 30) String phone,
            @NotBlank @Size(max = 500) String addressLine,
            @NotBlank @Size(max = 100) String city,
            @NotBlank @Size(max = 20) String postalCode,
            @Size(max = 500) String deliveryNote
    ) {
    }

    public record OrderItemResponse(
            Long id,
            Long productId,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal discount,
            BigDecimal totalPrice
    ) {
    }

    public record PaymentResponse(
            PaymentProvider provider,
            PaymentStatus status,
            BigDecimal amount,
            String transactionRef
    ) {
    }

    public record OrderResponse(
            Long id,
            String orderNumber,
            OrderStatus status,
            BigDecimal subtotal,
            BigDecimal discount,
            BigDecimal shippingCost,
            BigDecimal totalAmount,
            String shippingName,
            String shippingPhone,
            String shippingAddress,
            String shippingCity,
            String shippingPostalCode,
            String deliveryNote,
            List<OrderItemResponse> items,
            PaymentResponse payment,
            Instant createdAt
    ) {
    }

    public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {
    }
}
