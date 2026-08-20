package com.example.flowstate.repository;

import com.example.flowstate.model.Task;
import com.example.flowstate.model.TaskCategory;
import com.example.flowstate.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByTrackId(Long trackId);
    Page<Task> findByCategory(TaskCategory category, Pageable pageable);
    List<Task> findByStatusAndDateBefore(TaskStatus status, LocalDate date);
    long countByTrackIdAndStatusAndDate(Long trackId, TaskStatus status, LocalDate date);
}
