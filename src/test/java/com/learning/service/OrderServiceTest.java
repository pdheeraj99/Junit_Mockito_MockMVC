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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ ORDER SERVICE TEST (Coverage Booster) ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * Testing complex business logic in OrderService to maximize code coverage.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderService orderService;

    @Nested
    @DisplayName("Create Order Scenarios")
    class CreateOrder {

        @Test
        @DisplayName("Should create order successfully")
        void shouldCreateOrder() {
            // Given
            Long userId = 1L;
            User user = new User("Test User", "test@test.com", "pass");
            user.setId(userId);

            List<OrderItem> items = List.of(new OrderItem(1L, "Item 1", 1, BigDecimal.TEN));
            String address = "123 Street";

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(orderRepository.save(any(Order.class))).willAnswer(invocation -> {
                Order o = invocation.getArgument(0);
                o.setId(100L); // simulate DB id
                return o;
            });

            // When
            Order order = orderService.createOrder(userId, items, address);

            // Then
            assertThat(order.getId()).isEqualTo(100L);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(order.getTotalAmount()).isEqualTo(BigDecimal.TEN);
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("Should fail validation for inactive user")
        void shouldFailInactiveUser() {
            // Given
            Long userId = 1L;
            User user = new User("Inactive", "inactive@test.com", "pass");
            user.setActive(false); // Inactive

            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // When & Then
            assertThatThrownBy(() -> orderService.createOrder(userId, List.of(), "Addr"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("inactive user");
        }

        @Test
        @DisplayName("Should fail validation for empty items")
        void shouldFailEmptyItems() {
            // Given
            Long userId = 1L;
            User user = new User("User", "test@test.com", "pass");
            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // When & Then
            assertThatThrownBy(() -> orderService.createOrder(userId, Collections.emptyList(), "Addr"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("at least one item");
        }
    }

    @Nested
    @DisplayName("Process Payment Scenarios")
    class ProcessPayment {

        @Test
        @DisplayName("Should process successful payment")
        void shouldProcessPayment() {
            // Given
            Long orderId = 1L;
            Order order = new Order(1L, List.of(new OrderItem(1L, "Item", 1, BigDecimal.TEN)));
            order.setId(orderId);
            order.setStatus(OrderStatus.PENDING);
            order.setTotalAmount(BigDecimal.TEN);

            User user = new User("User", "test@test.com", "pass");

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(paymentGateway.processPayment(any(), any(), any()))
                    .willReturn(new PaymentResult(true, "txn_123", "Success"));
            given(orderRepository.save(any(Order.class))).willReturn(order);
            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            // When
            Order processed = orderService.processPayment(orderId, "token_123");

            // Then
            assertThat(processed.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
            assertThat(processed.getPaymentId()).isEqualTo("txn_123");
            verify(emailService).sendOrderConfirmation(eq("test@test.com"), eq(orderId), any());
        }

        @Test
        @DisplayName("Should fail payment if gateway rejects")
        void shouldFailPaymentRejection() {
            // Given
            Long orderId = 1L;
            Order order = new Order(1L, List.of(new OrderItem(1L, "Item", 1, BigDecimal.TEN)));
            order.setStatus(OrderStatus.PENDING);
            order.setTotalAmount(BigDecimal.TEN);

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(paymentGateway.processPayment(any(), any(), any()))
                    .willReturn(new PaymentResult(false, null, "Insufficient Funds"));

            // When & Then
            assertThatThrownBy(() -> orderService.processPayment(orderId, "token"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Payment failed");
        }
    }

    @Nested
    @DisplayName("Cancel Order Scenarios")
    class CancelOrder {

        @Test
        @DisplayName("Should cancel order successfully")
        void shouldCancelOrder() {
            // Given
            Long orderId = 1L;
            Order order = new Order(1L, List.of());
            order.setStatus(OrderStatus.PENDING);

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(orderRepository.save(any())).willReturn(order); // Return Mock

            // When
            Order cancelled = orderService.cancelOrder(orderId, "Changed mind");

            // Then
            assertThat(cancelled.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("Should not cancel SHIPPED order")
        void shouldNotCancelShipped() {
            Long orderId = 1L;
            Order order = new Order(1L, List.of());
            order.setStatus(OrderStatus.SHIPPED);

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            assertThatThrownBy(() -> orderService.cancelOrder(orderId, "Reason"))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
