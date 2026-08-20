package com.example.flowstate.service;

import com.example.flowstate.exception.TaskNotFoundException;
import com.example.flowstate.exception.TrackNotFoundException;
import com.example.flowstate.model.Task;
import com.example.flowstate.model.TaskCategory;
import com.example.flowstate.model.Track;
import com.example.flowstate.repository.TaskRepository;
import com.example.flowstate.repository.TrackRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TrackRepository trackRepository;

    public TaskService(TaskRepository taskRepository, TrackRepository trackRepository) {
        this.taskRepository = taskRepository;
        this.trackRepository = trackRepository;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Page<Task> findAll(TaskCategory category, Pageable pageable) {
        if (category == null) {
            return taskRepository.findAll(pageable);
        }
        return taskRepository.findByCategory(category, pageable);
    }

    public Task getById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public Task update(Long id, Task task) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.setId(id);
        task.setTrack(existing.getTrack());
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }

    public Task createTaskForTrack(Long trackId, Task task) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new TrackNotFoundException(trackId));
        task.setTrack(track);
        return taskRepository.save(task);
    }
}