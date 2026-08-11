package com.example.flowstate.dto.response;

import com.example.flowstate.model.TaskStatus;

public record TaskResponse(
        Long id,
        String title,
        TaskStatus status,
        int orderIndex
) {}