package com.example.flowstate.controller;

import com.example.flowstate.dto.request.TaskRequest;
import com.example.flowstate.dto.request.TrackRequest;
import com.example.flowstate.dto.response.*;
import com.example.flowstate.mapper.TaskWebMapper;
import com.example.flowstate.mapper.TrackWebMapper;
import com.example.flowstate.model.Task;
import com.example.flowstate.model.Track;
import com.example.flowstate.service.TaskService;
import com.example.flowstate.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
@Validated
public class TrackController {

    private final TrackService trackService;
    private final TrackWebMapper trackWebMapper;
    private final TaskService taskService;
    private final TaskWebMapper taskWebMapper;

    @GetMapping
    public Page<TrackResponse> getAll(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return trackService.findAll(pageable).map(trackWebMapper::toResponse);
    }

    @GetMapping("/{id}")
    public TrackResponse getById(@PathVariable("id") Long trackId) {
        return trackWebMapper.toResponse(trackService.getById(trackId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrackResponse create(@Valid @RequestBody TrackRequest request) {
        Track saved = trackService.save(trackWebMapper.toEntity(request));
        return trackWebMapper.toResponse(saved);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TrackResponse update(@PathVariable("id") Long trackId, @Valid @RequestBody TrackRequest request) {
        Track updated = trackService.update(trackId, trackWebMapper.toEntity(request));
        return trackWebMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long trackId) {
        trackService.delete(trackId);
    }

    @PostMapping("/{id}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTaskForTrack(@PathVariable("id") Long trackId, @Valid @RequestBody TaskRequest request) {
        Task saved = taskService.createTaskForTrack(trackId, taskWebMapper.toEntity(request));
        return taskWebMapper.toResponse(saved);
    }

    @GetMapping("/{id}/days")
    public List<DayResponse> getDays(@PathVariable("id") Long trackId) {
        return trackService.getDays(trackId);
    }
    @GetMapping("/{id}/progress")
    public ProgressResponse getProgress(@PathVariable("id") Long trackId){
        return trackService.getProgress(trackId);
    }
    @GetMapping("/{id}/streak-status")
    public StreakStatusResponse getStreakStatus(@PathVariable("id") Long trackId){
        return trackService.getStreakStatus(trackId);
    }
}