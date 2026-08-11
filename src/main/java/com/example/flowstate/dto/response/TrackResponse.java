package com.example.flowstate.dto.response;
import com.example.flowstate.model.TrackCategory;
import java.time.LocalDate;
public record TrackResponse (
        Long id,
        String title,
        TrackCategory category,
        LocalDate deadline,
        LocalDate createdAt
){}