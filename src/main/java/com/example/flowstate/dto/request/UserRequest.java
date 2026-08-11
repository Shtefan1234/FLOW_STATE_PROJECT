package com.example.flowstate.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Имя не может быть пустым")
        @Size(max = 255, message = "Имя слишком длинное")
        String name,

        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Некорректный email")
        @Size(max = 255, message = "Email слишком длинный")
        String email
) {}