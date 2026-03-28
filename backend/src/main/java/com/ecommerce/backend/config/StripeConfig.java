package com.ecommerce.backend.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Stripe payment gateway configuration.
 *
 * <p>Initializes the Stripe API key at application startup.
 * The Stripe Java SDK uses a global static key ({@link Stripe#apiKey}),
 * so we set it once during initialization.</p>
 *
 * <h3>Required environment variable:</h3>
 * <pre>{@code
 * STRIPE_API_KEY=sk_test_... (or sk_live_... for production)
 * }</pre>
 */
@Configuration
public class StripeConfig {

    private static final Logger logger = LoggerFactory.getLogger(StripeConfig.class);

    /** Stripe secret API key — loaded from application.yml / environment variable */
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    /**
     * Sets the Stripe API key after the bean is constructed.
     * {@code @PostConstruct} ensures this runs after dependency injection
     * but before the application starts handling requests.
     */
    @PostConstruct
    public void initStripe() {
        Stripe.apiKey = stripeApiKey;
        logger.info("Stripe API initialized successfully");
    }
}
