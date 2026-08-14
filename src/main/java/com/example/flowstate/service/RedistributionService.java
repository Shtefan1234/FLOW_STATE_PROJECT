package com.example.flowstate.service;

import com.example.flowstate.model.Task;
import com.example.flowstate.model.TaskStatus;
import com.example.flowstate.model.Track;
import com.example.flowstate.repository.TaskRepository;
import com.example.flowstate.repository.TrackRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
public class RedistributionService {
    private final TrackRepository trackRepository;
    private final TaskRepository taskRepository;
    public RedistributionService(TrackRepository trackRepository,TaskRepository taskRepository){
        this.taskRepository=taskRepository;
        this.trackRepository=trackRepository;
    }
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void redistributeOverdueTasks(){
        LocalDate today = LocalDate.now();
        List<Track> tracks = trackRepository.findAll();

        for (Track track : tracks) {
            if (track.getDeadline() != null && track.getDeadline().isBefore(today)) {
                continue;
            }

            List<Task> overdue = taskRepository.findByStatusAndDateBefore(TaskStatus.PENDING, today)
                    .stream()
                    .filter(t -> t.getTrack() != null && t.getTrack().getId().equals(track.getId()))
                    .sorted(Comparator.comparing(Task::getDate))
                    .toList();
            if(overdue.isEmpty()){
                continue;
            }
            LocalDate end = (track.getDeadline() != null)
                    ? track.getDeadline()
                    : lastPlannedDate(track);
            if (end == null){
                continue;
            }

            List<LocalDate> targetDays = IntStream.rangeClosed(0, (int) ChronoUnit.DAYS.between(today, end))
                    .mapToObj(today::plusDays)
                    .toList();

            for (int i = 0; i < overdue.size(); i++) {
                overdue.get(i).setDate(targetDays.get(i % targetDays.size()));
            }
            taskRepository.saveAll(overdue);
        }
    }

    private LocalDate lastPlannedDate(Track track) {
        return taskRepository.findByTrackId(track.getId()).stream()
                .map(Task::getDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);
    }
}