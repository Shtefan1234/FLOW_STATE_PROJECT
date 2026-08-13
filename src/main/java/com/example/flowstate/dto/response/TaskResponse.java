package com.example.flowstate.dto.response;

import com.example.flowstate.model.TaskCategory;
import com.example.flowstate.model.TaskStatus;

import java.time.LocalDate;

public record TaskResponse(
        Long id,
        String title,
        TaskStatus status,
        TaskCategory category,
        LocalDate date,
        int orderIndex
) {}