package com.learning.service;

import com.learning.external.EmailService;
import com.learning.external.PaymentGateway;
import com.learning.external.PaymentGateway.PaymentResult;
import com.learning.model.Order;
import com.learning.model.Order.OrderStatus;
import com.learning.model.OrderItem;
import com.learning.model.User;
import com.learning.repository.OrderRepository;
import com.learning.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * OrderService - Complex business logic for orders
 * 
 * Multiple dependencies to mock:
 * - OrderRepository (database)
 * - UserRepository (database)
 * - PaymentGateway (external payment)
 * - EmailService (external email)
 */
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentGateway paymentGateway;
    private final EmailService emailService;

    public OrderService(OrderRepository orderRepository,
            UserRepository userRepository,
            PaymentGateway paymentGateway,
            EmailService emailService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.paymentGateway = paymentGateway;
        this.emailService = emailService;
    }

    /**
     * Create a new order
     * 
     * Complex flow:
     * 1. Validate user exists
     * 2. Validate items not empty
     * 3. Calculate total
     * 4. Create order
     * 5. Return pending order
     */
    public Order createOrder(Long userId, List<OrderItem> items, String shippingAddress) {
        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (!user.isActive()) {
            throw new IllegalStateException("Cannot create order for inactive user");
        }

        // Validate items
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        // Validate shipping address
        if (shippingAddress == null || shippingAddress.isBlank()) {
            throw new IllegalArgumentException("Shipping address is required");
        }

        // Create order
        Order order = new Order(userId, items);
        order.setShippingAddress(shippingAddress);
        order.setTotalAmount(order.calculateTotal());
        order.setStatus(OrderStatus.PENDING);

        return orderRepository.save(order);
    }

    /**
     * Process payment for an order
     * 
     * Complex flow:
     * 1. Find order
     * 2. Verify order is pending
     * 3. Process payment
     * 4. Update order status
     * 5. Send confirmation email
     */
    public Order processPayment(Long orderId, String cardToken) {
        // Find order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // Verify status
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order is not pending payment: " + order.getStatus());
        }

        // Process payment
        PaymentResult result = paymentGateway.processPayment(
                order.getTotalAmount(),
                "INR",
                cardToken);

        if (!result.success()) {
            throw new RuntimeException("Payment failed: " + result.message());
        }

        // Update order
        order.setPaymentId(result.transactionId());
        order.setStatus(OrderStatus.CONFIRMED);
        Order savedOrder = orderRepository.save(order);

        // Send confirmation email
        User user = userRepository.findById(order.getUserId()).orElse(null);
        if (user != null) {
            emailService.sendOrderConfirmation(
                    user.getEmail(),
                    orderId,
                    order.getTotalAmount().toString());
        }

        return savedOrder;
    }

    /**
     * Cancel an order with refund
     */
    public Order cancelOrder(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // Can only cancel if not shipped
        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel shipped/delivered order");
        }

        // Refund if payment was made
        if (order.getPaymentId() != null) {
            PaymentResult refund = paymentGateway.refundPayment(
                    order.getPaymentId(),
                    order.getTotalAmount());

            if (!refund.success()) {
                throw new RuntimeException("Refund failed: " + refund.message());
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    /**
     * Ship an order
     */
    public Order shipOrder(Long orderId, String trackingNumber) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.CONFIRMED &&
                order.getStatus() != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Order not ready for shipping: " + order.getStatus());
        }

        order.setStatus(OrderStatus.SHIPPED);
        Order savedOrder = orderRepository.save(order);

        // Send shipping notification
        User user = userRepository.findById(order.getUserId()).orElse(null);
        if (user != null) {
            emailService.sendShippingNotification(
                    user.getEmail(),
                    orderId,
                    trackingNumber);
        }

        return savedOrder;
    }

    /**
     * Get user's orders
     */
    public List<Order> getUserOrders(Long userId) {
        // Verify user exists
        if (!userRepository.findById(userId).isPresent()) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return orderRepository.findByUserId(userId);
    }

    /**
     * Get order by ID
     */
    public Optional<Order> getOrder(Long orderId) {
        return orderRepository.findById(orderId);
    }

    /**
     * Calculate order total with discount
     */
    public BigDecimal calculateTotalWithDiscount(Long orderId, int discountPercent) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Invalid discount: " + discountPercent);
        }

        BigDecimal total = order.getTotalAmount();
        BigDecimal discount = total.multiply(BigDecimal.valueOf(discountPercent))
                .divide(BigDecimal.valueOf(100));

        return total.subtract(discount);
    }
}
