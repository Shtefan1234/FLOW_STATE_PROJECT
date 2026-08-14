package com.example.flowstate.dto.response;

public record ProgressResponse(
        int totalTasks,
        int doneTasks,
        int totalDays,
        int doneDays
) {}
