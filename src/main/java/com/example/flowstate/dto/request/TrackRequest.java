package com.example.flowstate.dto.request;
import com.example.flowstate.model.TrackCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TrackRequest(
    @NotBlank(message = "Название трека не может быть пустым")
    @Size(max = 255, message = "Название трека слишком длинное")
    String title,
    @NotNull(message = "Категория обязательна")
    TrackCategory category,
    LocalDate deadline
) {}

