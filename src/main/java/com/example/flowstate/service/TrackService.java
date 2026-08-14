package com.example.flowstate.service;

import com.example.flowstate.dto.response.DayResponse;
import com.example.flowstate.dto.response.ProgressResponse;
import com.example.flowstate.exception.TrackNotFoundException;
import com.example.flowstate.exception.UserNotFoundException;
import com.example.flowstate.model.DayStatus;
import com.example.flowstate.model.Task;
import com.example.flowstate.model.TaskStatus;
import com.example.flowstate.model.Track;
import com.example.flowstate.model.User;
import com.example.flowstate.repository.TaskRepository;
import com.example.flowstate.repository.TrackRepository;
import com.example.flowstate.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public TrackService(TrackRepository trackRepository, UserRepository userRepository, TaskRepository taskRepository) {
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public List<Track> findAll() {
        return trackRepository.findAll();
    }

    public Track getById(Long id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new TrackNotFoundException(id));
    }

    public Track save(Track track) {
        return trackRepository.save(track);
    }

    public Track update(Long id, Track track) {
        if (!trackRepository.existsById(id)) {
            throw new TrackNotFoundException(id);
        }
        track.setId(id);
        return trackRepository.save(track);
    }

    public void delete(Long id) {
        if (!trackRepository.existsById(id)) {
            throw new TrackNotFoundException(id);
        }
        trackRepository.deleteById(id);
    }

    public Track createTrackForUser(Long userId, Track trackData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        trackData.setUser(user);
        if (trackData.getCreatedAt() == null) {
            trackData.setCreatedAt(LocalDate.now());
        }
        return trackRepository.save(trackData);
    }
    public Page<Track> findAll(Pageable pageable) {
        return trackRepository.findAll(pageable);
    }
    public List<DayResponse> getDays(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new TrackNotFoundException(trackId));

        List<Task> tasks = taskRepository.findByTrackId(trackId);

        Map<LocalDate, List<Task>> byDate = tasks.stream()
                .filter(t -> t.getDate() != null)
                .collect(Collectors.groupingBy(Task::getDate));

        LocalDate start = track.getCreatedAt();

        LocalDate end;
        if (track.getDeadline() != null) {
            end = track.getDeadline();
        } else {
            LocalDate lastPlannedDate = tasks.stream()
                    .filter(t -> t.getDate() != null)
                    .map(Task::getDate)
                    .max(LocalDate::compareTo)
                    .orElse(LocalDate.now());
            end = lastPlannedDate.isAfter(LocalDate.now()) ? lastPlannedDate : LocalDate.now();
        }

        if (end.isBefore(start)) {
            return List.of();
        }

        long daysCount = ChronoUnit.DAYS.between(start, end) + 1;

        return IntStream.rangeClosed(0, (int) daysCount - 1)
                .mapToObj(start::plusDays)
                .map(date -> buildDay(date, byDate.getOrDefault(date, List.of())))
                .toList();
    }

    private DayResponse buildDay(LocalDate date, List<Task> tasks) {
        int done = (int) tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE || t.getStatus() == TaskStatus.SKIPPED)
                .count();
        DayStatus status;
        if (tasks.isEmpty()) {
            status = DayStatus.EMPTY;
        } else if (done == tasks.size()) {
            status = DayStatus.DONE;
        } else {
            status = DayStatus.PARTIAL;
        }
        return new DayResponse(date, status, tasks.size(), done);
    }
    public ProgressResponse getProgress(Long trackId){
        List<DayResponse> days = getDays(trackId);

        int totalDays = days.size();
        int doneDays = (int) days.stream()
                .filter(e->e.status()==DayStatus.DONE)
                .count();
        int totalTasks = days.stream().mapToInt(DayResponse::totalTasks).sum();
        int doneTasks = days.stream().mapToInt(DayResponse::doneTasks).sum();
        return new ProgressResponse(totalTasks,doneTasks,totalDays,doneDays);
    }
}