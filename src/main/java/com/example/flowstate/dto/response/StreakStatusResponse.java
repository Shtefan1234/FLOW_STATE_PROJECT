package com.example.flowstate.dto.response;

public record StreakStatusResponse(
        int currentStreak,
        boolean atRisk,
        int pendingTaskToday,
        int hoursLeftToday
        ) {}
