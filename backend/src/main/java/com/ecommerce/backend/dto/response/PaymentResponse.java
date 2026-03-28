package com.ecommerce.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Stripe payment checkout session response.
 *
 * <p>Contains the Stripe session ID and the redirect URL. The frontend
 * should redirect the user to {@code sessionUrl} to complete payment
 * on Stripe's hosted checkout page.</p>
 *
 * <h3>Frontend usage:</h3>
 * <pre>{@code
 * // After receiving this response:
 * window.location.href = response.data.sessionUrl;
 * }</pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    /** Stripe Checkout Session ID — used for tracking and reconciliation */
    private String sessionId;

    /** URL to redirect the user to Stripe's hosted checkout page */
    private String sessionUrl;
}
