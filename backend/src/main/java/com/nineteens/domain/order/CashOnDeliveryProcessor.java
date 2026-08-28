package com.nineteens.domain.order;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CashOnDeliveryProcessor implements PaymentProcessor {

    @Override
    public PaymentProvider provider() {
        return PaymentProvider.CASH_ON_DELIVERY;
    }

    @Override
    public Payment initiate(Order order) {
        Payment payment = new Payment();
        payment.setProvider(PaymentProvider.CASH_ON_DELIVERY);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(order.getTotalAmount());
        payment.setTransactionRef("COD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return payment;
    }
}
