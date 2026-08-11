package com.example.flowstate.mapper;

import com.example.flowstate.dto.request.TaskRequest;
import com.example.flowstate.dto.response.TaskResponse;
import com.example.flowstate.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskWebMapper {

    public Task toEntity(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setStatus(request.status());
        task.setOrderIndex(request.orderIndex());
        return task;
    }

    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getStatus(),
                task.getOrderIndex()
        );
    }
}