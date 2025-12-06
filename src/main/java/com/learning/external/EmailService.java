package com.learning.external;

/**
 * EmailService - External email sending service
 * 
 * In real world:
 * - Could be SendGrid, Mailchimp, AWS SES, etc.
 * - We DON'T want to send real emails in tests
 * - We MOCK this and just VERIFY it was called correctly
 */
public interface EmailService {

    /**
     * Send welcome email to new user
     * 
     * @param toEmail  Recipient email
     * @param userName User's name
     * @return true if sent successfully
     */
    boolean sendWelcomeEmail(String toEmail, String userName);

    /**
     * Send password reset email
     * 
     * @param toEmail    Recipient email
     * @param resetToken Password reset token
     * @return true if sent successfully
     */
    boolean sendPasswordResetEmail(String toEmail, String resetToken);

    /**
     * Send order confirmation email
     * 
     * @param toEmail     Recipient email
     * @param orderId     Order ID
     * @param totalAmount Order total
     * @return true if sent successfully
     */
    boolean sendOrderConfirmation(String toEmail, Long orderId, String totalAmount);

    /**
     * Send order shipped notification
     * 
     * @param toEmail        Recipient email
     * @param orderId        Order ID
     * @param trackingNumber Shipping tracking number
     * @return true if sent successfully
     */
    boolean sendShippingNotification(String toEmail, Long orderId, String trackingNumber);
}
