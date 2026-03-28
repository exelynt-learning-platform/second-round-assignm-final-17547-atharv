package com.ecommerce.backend.entity.enums;

/**
 * Enumeration representing the lifecycle stages of an order.
 * 
 * <p>Order flow: PENDING → PAID → SHIPPED → DELIVERED</p>
 * <p>An order can be CANCELLED at any stage before DELIVERED.</p>
 */
public enum OrderStatus {

    /** Order has been created but payment has not been processed yet */
    PENDING,

    /** Payment has been successfully processed via the payment gateway */
    PAID,

    /** Order has been handed off to the shipping carrier */
    SHIPPED,

    /** Order has been delivered to the customer */
    DELIVERED,

    /** Order was cancelled by the customer or an admin */
    CANCELLED
}
