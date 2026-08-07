package com.example.flowstate.controller;
import com.example.flowstate.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.flowstate.model.Task;
import com.example.flowstate.repository.TaskRepository;
import java.util.List;
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService){
        this.taskService=taskService;
    }
    @GetMapping
    public List<Task> getAll(){
        return taskService.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable Long id){
        Task task = taskService.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(task);
    }
    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task task){
        Task saved = taskService.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id,@RequestBody Task task){
        if(!taskService.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        task.setId(id);
        return ResponseEntity.ok(taskService.save(task));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        if(!taskService.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/api/tracks/{id}/tasks")
    public ResponseEntity<Task> createForTrack(@PathVariable Long id, @RequestBody Task task){
        Task saved = taskService.createTaskForTrack(id, task);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
