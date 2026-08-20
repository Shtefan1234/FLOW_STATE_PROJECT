package com.example.flowstate.service;

import com.example.flowstate.model.Task;
import com.example.flowstate.model.TaskStatus;
import com.example.flowstate.model.Track;
import com.example.flowstate.repository.TaskRepository;
import com.example.flowstate.repository.TrackRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StreakService {
    private final TaskRepository taskRepository;
    private final TrackRepository trackRepository;

    public StreakService(TrackRepository trackRepository,TaskRepository taskRepository){
        this.trackRepository=trackRepository;
        this.taskRepository=taskRepository;
    }

    @Scheduled(cron = "0 59 23 * * *")
    @Transactional
    public void updateAllStreaks(){
        LocalDate today = LocalDate.now();
        for(Track track : trackRepository.findAll()){
            updateStreak(track,today);
        }
    }

    private void updateStreak(Track track,LocalDate today){
        List<Task> todayTasks = taskRepository.findByTrackId(track.getId()).stream()
                .filter(t-> today.equals(t.getDate()))
                .toList();
        if (todayTasks.isEmpty()) return;

        boolean hasDone = todayTasks.stream().anyMatch(t->t.getStatus() == TaskStatus.DONE);
        if(hasDone){
            LocalDate last = track.getLastActiveDate();
            if(today.equals(last)) return;
            if(today.minusDays(1).equals(last)){
                track.setCurrentStreak(track.getCurrentStreak()+1);
            } else {
                track.setCurrentStreak(1);
            }
            track.setLastActiveDate(today);
        }
        else{
            track.setCurrentStreak(0);
        }
        trackRepository.save(track);
    }
}
