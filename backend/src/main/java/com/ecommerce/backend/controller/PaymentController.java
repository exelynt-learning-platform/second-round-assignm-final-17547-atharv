package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.dto.response.PaymentResponse;
import com.ecommerce.backend.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Stripe payment integration.
 *
 * <h3>Endpoints:</h3>
 * <ul>
 *   <li>{@code POST /api/payments/checkout/{orderId}} — creates a Stripe Checkout Session</li>
 *   <li>{@code POST /api/payments/webhook} — receives Stripe webhook events</li>
 * </ul>
 *
 * <h3>Payment flow:</h3>
 * <ol>
 *   <li>User places an order (status: PENDING)</li>
 *   <li>Frontend calls checkout endpoint to get Stripe session URL</li>
 *   <li>Frontend redirects user to Stripe checkout page</li>
 *   <li>User completes payment on Stripe</li>
 *   <li>Stripe sends webhook → order status updated to PAID</li>
 * </ol>
 *
 * <p>The webhook endpoint is publicly accessible (no JWT required)
 * because Stripe sends the request directly. Authentication is handled
 * via Stripe signature verification in the service layer.</p>
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Creates a Stripe Checkout Session for the given order.
     * Returns a session URL that the frontend should redirect to.
     *
     * @param orderId the order to create a payment session for
     * @return PaymentResponse with sessionId and sessionUrl
     */
    @PostMapping("/checkout/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> createCheckoutSession(
            @PathVariable Long orderId) {
        PaymentResponse response = paymentService.createCheckoutSession(orderId);
        return ResponseEntity.ok(ApiResponse.success(response, "Checkout session created"));
    }

    /**
     * Receives and processes Stripe webhook events.
     *
     * <p>This endpoint is called by Stripe's servers (not by our frontend).
     * The raw request body and Stripe-Signature header are passed to the
     * service for signature verification and event processing.</p>
     *
     * <p>Important: The request body must be read as raw String (not parsed as JSON)
     * because Stripe signature verification requires the exact byte-for-byte payload.</p>
     *
     * @param payload   the raw webhook request body
     * @param sigHeader the Stripe-Signature header for verification
     * @return 200 OK to acknowledge receipt (Stripe will retry on non-2xx)
     */
    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<Void>> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        paymentService.handleWebhook(payload, sigHeader);
        return ResponseEntity.ok(ApiResponse.success(null, "Webhook processed successfully"));
    }
}
