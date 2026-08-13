package com.example.flowstate.dto.request;

import com.example.flowstate.model.TaskCategory;
import com.example.flowstate.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank(message = "Название задачи не может быть пустым")
        @Size(max = 255, message = "Название задачи слишком длинное")
        String title,

        @NotNull(message = "Статус обязателен")
        TaskStatus status,

        @NotNull(message = "Категория обязательна")
        TaskCategory category,

        @NotNull(message = "Дата обязательна")
        LocalDate date,

        int orderIndex
) {}