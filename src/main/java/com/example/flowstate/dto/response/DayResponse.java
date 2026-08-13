package com.example.flowstate.dto.response;

import com.example.flowstate.model.DayStatus;
import java.time.LocalDate;

public record DayResponse(
        LocalDate date,
        DayStatus status,
        int totalTasks,
        int doneTasks
) {}
