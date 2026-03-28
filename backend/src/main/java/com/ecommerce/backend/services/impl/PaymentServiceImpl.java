package com.ecommerce.backend.services.impl;

import com.ecommerce.backend.dto.response.PaymentResponse;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.OrderItem;
import com.ecommerce.backend.entity.enums.OrderStatus;
import com.ecommerce.backend.exception.PaymentException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.services.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link PaymentService} — integrates with Stripe payment gateway.
 *
 * <h3>Checkout flow:</h3>
 * <ol>
 *   <li>Frontend calls {@code POST /api/payments/checkout/{orderId}}</li>
 *   <li>This service creates a Stripe Checkout Session with line items from the order</li>
 *   <li>Returns the session URL — frontend redirects user to Stripe's hosted checkout</li>
 *   <li>User completes payment on Stripe</li>
 *   <li>Stripe sends a webhook to {@code POST /api/payments/webhook}</li>
 *   <li>This service verifies the webhook signature and updates the order to PAID</li>
 * </ol>
 *
 * <h3>Why Stripe Checkout (hosted page)?</h3>
 * <ul>
 *   <li>PCI compliance: card details never touch our servers</li>
 *   <li>Built-in fraud detection and 3D Secure support</li>
 *   <li>Professional payment UI with minimal integration effort</li>
 * </ul>
 */
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final OrderRepository orderRepository;

    /** Stripe webhook signing secret — used to verify webhook authenticity */
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    /** URL to redirect the customer to after a successful payment */
    @Value("${stripe.success-url}")
    private String successUrl;

    /** URL to redirect the customer to if they cancel payment */
    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    public PaymentServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public PaymentResponse createCheckoutSession(Long orderId) {
        // Step 1: Fetch the order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // Step 2: Validate order is in PENDING status (not already paid)
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new PaymentException("Order is not in PENDING status. Current status: " + order.getStatus());
        }

        try {
            // Step 3: Build Stripe Checkout Session parameters
            SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)       // One-time payment
                    .setSuccessUrl(successUrl)                        // Redirect on success
                    .setCancelUrl(cancelUrl)                          // Redirect on cancel
                    .putMetadata("orderId", orderId.toString());      // Link session to our order

            // Step 4: Add each order item as a line item in the Stripe session
            for (OrderItem item : order.getOrderItems()) {
                paramsBuilder.addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity((long) item.getQuantity())
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                // Stripe expects amounts in cents (smallest currency unit)
                                                .setUnitAmount(item.getPriceAtPurchase()
                                                        .multiply(java.math.BigDecimal.valueOf(100))
                                                        .longValue())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(item.getProduct().getName())
                                                                .setDescription(item.getProduct().getDescription())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                );
            }

            // Step 5: Create the Stripe Checkout Session
            Session session = Session.create(paramsBuilder.build());

            // Step 6: Save the Stripe session ID on the order for webhook reconciliation
            order.setStripeSessionId(session.getId());
            orderRepository.save(order);

            logger.info("Stripe checkout session created for order {}: {}", orderId, session.getId());

            // Step 7: Return the session details to the frontend
            return PaymentResponse.builder()
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();

        } catch (StripeException e) {
            logger.error("Stripe API error while creating checkout session for order {}: {}",
                    orderId, e.getMessage(), e);
            throw new PaymentException("Failed to create payment session: " + e.getMessage(), e);
        }
    }

    @Override
    public void handleWebhook(String payload, String sigHeader) {
        Event event;

        try {
            // Step 1: Verify the webhook signature to prevent spoofing
            // Stripe signs each webhook with the webhook secret — this verifies authenticity
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            logger.error("Stripe webhook signature verification failed: {}", e.getMessage());
            throw new PaymentException("Invalid webhook signature", e);
        }

        // Step 2: Handle the event based on its type
        if ("checkout.session.completed".equals(event.getType())) {
            // Payment was successful — update the order status
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow(() -> new PaymentException("Failed to deserialize Stripe session"));

            String sessionId = session.getId();

            // Step 3: Find the order by Stripe session ID and update status to PAID
            Order order = orderRepository.findByStripeSessionId(sessionId)
                    .orElseThrow(() -> {
                        logger.error("No order found for Stripe session: {}", sessionId);
                        return new ResourceNotFoundException("Order", "stripeSessionId", sessionId);
                    });

            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);

            logger.info("Payment completed for order {}: session {}", order.getId(), sessionId);

        } else {
            // Log other event types for debugging (e.g., payment_intent.failed)
            logger.info("Received Stripe webhook event: {} (unhandled)", event.getType());
        }
    }
}
