package com.learning.model;

/**
 * Enum for testing @EnumSource annotation in JUnit 5
 */
public enum DayOfWeek {
    MONDAY(false),
    TUESDAY(false),
    WEDNESDAY(false),
    THURSDAY(false),
    FRIDAY(false),
    SATURDAY(true),
    SUNDAY(true);

    private final boolean weekend;

    DayOfWeek(boolean weekend) {
        this.weekend = weekend;
    }

    public boolean isWeekend() {
        return weekend;
    }

    public boolean isWeekday() {
        return !weekend;
    }
}
