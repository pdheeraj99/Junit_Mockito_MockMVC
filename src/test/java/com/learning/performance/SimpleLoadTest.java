package com.learning.performance;

import com.learning.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.learning.external.EmailService;
import com.learning.external.PaymentGateway;
import com.learning.repository.UserRepository;

/**
 * Load Test running as an Integration Test.
 */
public class SimpleLoadTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @MockBean
    private EmailService emailService;

    @MockBean
    private PaymentGateway paymentGateway;

    @Autowired
    private UserRepository userRepository;

    private static final int USER_COUNT = 100;
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Counters
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failCount = new AtomicInteger(0);

    @Test
    public void runLoadTest() throws InterruptedException {
        String apiUrl = "http://localhost:" + port + "/api/users";

        System.out.println("\n🔥 STARTING LOAD TEST 🔥");
        System.out.println("Target: " + apiUrl);
        System.out.println("Users: " + USER_COUNT);

        long initialCount = userRepository.count();
        System.out.println("📊 Initial DB Count (in Docker MySQL): " + initialCount);

        ExecutorService executor = Executors.newFixedThreadPool(USER_COUNT);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < USER_COUNT; i++) {
            executor.submit(() -> {
                try {
                    sendRequest(apiUrl);
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.err.println("Request Failed: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        long finalCount = userRepository.count();

        System.out.println("\n✅ LOAD TEST COMPLETED");
        System.out.println("Time Taken: " + duration + "ms");
        System.out.println("Successful Requests: " + successCount.get());
        System.out.println("Failed Requests: " + failCount.get());
        System.out.println("📊 Final DB Count (in Docker MySQL): " + finalCount);
        System.out.println("🎉 Records Created: " + (finalCount - initialCount));

        double throughput = successCount.get() / (duration / 1000.0);
        System.out.println("Throughput: " + String.format("%.2f", throughput) + " req/sec");
    }

    private void sendRequest(String url) throws Exception {
        String randomName = "LoadUser" + new Random().nextInt(100000);
        String randomEmail = "load" + new Random().nextInt(100000) + "@test.com";

        String jsonBody = """
                {
                    "name": "%s",
                    "email": "%s",
                    "password": "password123"
                }
                """.formatted(randomName, randomEmail);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201 || response.statusCode() == 200) {
            int current = successCount.incrementAndGet();
            if (current % 10 == 0) {
                System.out.print("."); // Progress bar
            }
        } else {
            failCount.incrementAndGet();
            System.out.println("\nError " + response.statusCode() + ": " + response.body());
        }
    }
}
