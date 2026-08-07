package com.example.flowstate.service;
import com.example.flowstate.model.Task;
import com.example.flowstate.model.Track;
import com.example.flowstate.repository.TaskRepository;
import com.example.flowstate.repository.TrackRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TrackRepository trackRepository;
    public TaskService(TrackRepository trackRepository,TaskRepository taskRepository){
        this.taskRepository=taskRepository;
        this.trackRepository=trackRepository;
    }
    public List<Task> findAll(){
        return taskRepository.findAll();
    }
    public Optional<Task> findById(Long id){
        return taskRepository.findById(id);
    }
    public Task save(Task task){
        return taskRepository.save(task);
    }
    public boolean existsById(Long id){
        return taskRepository.existsById(id);
    }
    public void deleteById(Long id){
        taskRepository.deleteById(id);
    }
    public Task createTaskForTrack(Long trackId, Task task){
        Track track = trackRepository.findById(trackId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        task.setTrack(track);
        return taskRepository.save(task);
    }

}
