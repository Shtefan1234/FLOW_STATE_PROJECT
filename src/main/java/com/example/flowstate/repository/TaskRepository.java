package com.example.flowstate.repository;

import com.example.flowstate.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByTrackId(Long trackId);
}
