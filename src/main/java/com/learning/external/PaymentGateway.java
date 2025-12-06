package com.learning.external;

import java.math.BigDecimal;

/**
 * PaymentGateway - External payment processing service
 * 
 * In real world:
 * - Could be Stripe, Razorpay, PayPal, etc.
 * - We NEVER want to charge real money in tests!
 * - Perfect candidate for mocking
 */
public interface PaymentGateway {

    /**
     * Payment result containing status and transaction ID
     */
    record PaymentResult(boolean success, String transactionId, String message) {
    }

    /**
     * Process a payment
     * 
     * @param amount    Amount to charge
     * @param currency  Currency code (INR, USD, etc.)
     * @param cardToken Tokenized card details
     * @return Payment result
     */
    PaymentResult processPayment(BigDecimal amount, String currency, String cardToken);

    /**
     * Refund a payment
     * 
     * @param transactionId Original transaction ID
     * @param amount        Amount to refund
     * @return Payment result for refund
     */
    PaymentResult refundPayment(String transactionId, BigDecimal amount);

    /**
     * Verify payment status
     * 
     * @param transactionId Transaction ID to verify
     * @return true if payment is confirmed
     */
    boolean verifyPayment(String transactionId);
}
