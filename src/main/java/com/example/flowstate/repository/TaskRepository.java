package com.example.flowstate.repository;

import com.example.flowstate.model.Task;
import com.example.flowstate.model.TaskCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByTrackId(Long trackId);
    Page<Task> findByCategory(TaskCategory category, Pageable pageable);
}
