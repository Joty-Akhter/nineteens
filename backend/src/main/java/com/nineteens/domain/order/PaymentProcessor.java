package com.nineteens.domain.order;

/**
 * Extension point for future gateways (Stripe, SSLCommerz, bKash, Nagad).
 * Cash on Delivery is the only production implementation in the first release.
 */
public interface PaymentProcessor {

    PaymentProvider provider();

    Payment initiate(Order order);
}
