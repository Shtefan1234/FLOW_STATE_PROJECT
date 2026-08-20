package com.example.flowstate.dto.response;
import java.time.LocalDate;
public record TrackResponse (
        Long id,
        String title,
        LocalDate deadline,
LocalDate createdAt,
        int currentStreak
){}