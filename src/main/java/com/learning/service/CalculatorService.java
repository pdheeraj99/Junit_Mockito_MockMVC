package com.learning.service;

import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Calculator Service - A simple service to demonstrate JUnit 5 testing
 * Each method has specific behavior that we'll test with different annotations
 */
@Service
public class CalculatorService {

    // ===== Basic Operations (for basic assertions) =====
    
    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public double divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero!");
        }
        return (double) a / b;
    }

    // ===== For assertNull / assertNotNull =====
    
    public String processInput(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        return input.toUpperCase();
    }

    // ===== For assertTrue / assertFalse =====
    
    public boolean isPositive(int number) {
        return number > 0;
    }

    public boolean isEven(int number) {
        return number % 2 == 0;
    }

    public boolean isPrime(int number) {
        if (number <= 1) return false;
        if (number <= 3) return true;
        if (number % 2 == 0 || number % 3 == 0) return false;
        for (int i = 5; i * i <= number; i += 6) {
            if (number % i == 0 || number % (i + 2) == 0) return false;
        }
        return true;
    }

    // ===== For assertArrayEquals =====
    
    public int[] getSquares(int... numbers) {
        int[] result = new int[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            result[i] = numbers[i] * numbers[i];
        }
        return result;
    }

    // ===== For assertIterableEquals =====
    
    public List<Integer> getMultiples(int number, int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .map(i -> number * i)
                .boxed()
                .toList();
    }

    // ===== For Exception Testing (assertThrows) =====
    
    public int factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number must be non-negative");
        }
        if (n > 20) {
            throw new ArithmeticException("Number too large, may cause overflow");
        }
        int result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    // ===== For Parameterized Tests =====
    
    public String getGrade(int score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    public int sumOfDigits(int number) {
        number = Math.abs(number);
        int sum = 0;
        while (number > 0) {
            sum += number % 10;
            number /= 10;
        }
        return sum;
    }
}
