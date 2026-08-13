package com.example.flowstate.controller;

import com.example.flowstate.dto.request.TaskRequest;
import com.example.flowstate.dto.response.TaskResponse;
import com.example.flowstate.mapper.TaskWebMapper;
import com.example.flowstate.model.Task;
import com.example.flowstate.model.TaskCategory;
import com.example.flowstate.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Validated
public class TaskController {

    private final TaskService taskService;
    private final TaskWebMapper taskWebMapper;

    @GetMapping
    public Page<TaskResponse> getAll(@RequestParam(required = false) TaskCategory category,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return taskService.findAll(category, pageable).map(taskWebMapper::toResponse);
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable("id") Long taskId) {
        return taskWebMapper.toResponse(taskService.getById(taskId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody TaskRequest request) {
        Task saved = taskService.save(taskWebMapper.toEntity(request));
        return taskWebMapper.toResponse(saved);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse update(@PathVariable("id") Long taskId, @Valid @RequestBody TaskRequest request) {
        Task updated = taskService.update(taskId, taskWebMapper.toEntity(request));
        return taskWebMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long taskId) {
        taskService.delete(taskId);
    }
}