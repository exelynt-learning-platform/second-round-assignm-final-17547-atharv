package com.ecommerce.backend.services;

import com.ecommerce.backend.dto.response.PaymentResponse;

/**
 * Service interface for Stripe payment gateway integration.
 *
 * <p>Handles creating Stripe Checkout Sessions for orders and
 * processing incoming webhook events for payment confirmation.</p>
 */
public interface PaymentService {

    /**
     * Creates a Stripe Checkout Session for the given order.
     * Returns the session URL to redirect the user to Stripe's hosted checkout page.
     *
     * @param orderId the order's ID (must be in PENDING status)
     * @return PaymentResponse with Stripe session ID and checkout URL
     * @throws com.ecommerce.backend.exception.ResourceNotFoundException if order not found
     * @throws com.ecommerce.backend.exception.PaymentException if Stripe session creation fails
     */
    PaymentResponse createCheckoutSession(Long orderId);

    /**
     * Processes a Stripe webhook event.
     * Verifies the webhook signature, and if the event is a checkout.session.completed,
     * updates the corresponding order status to PAID.
     *
     * @param payload   the raw webhook request body
     * @param sigHeader the Stripe-Signature header value
     * @throws com.ecommerce.backend.exception.PaymentException if signature verification fails
     */
    void handleWebhook(String payload, String sigHeader);
}
