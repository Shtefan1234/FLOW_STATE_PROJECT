package com.example.flowstate.service;

import com.example.flowstate.model.Track;
import com.example.flowstate.model.User;
import com.example.flowstate.repository.TrackRepository;
import com.example.flowstate.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TrackService {
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    public TrackService(TrackRepository trackRepository, UserRepository userRepository) {
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
    }

    public List<Track> findAll() {
        return trackRepository.findAll();
    }

    public Optional<Track> findById(Long id) {
        return trackRepository.findById(id);
    }

    public Track save(Track track) {
        return trackRepository.save(track);
    }

    public boolean existsById(Long id) {
        return trackRepository.existsById(id);
    }

    public void deleteById(Long id) {
        trackRepository.deleteById(id);
    }
    public Track createTrackForUser(Long userId, Track trackData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        trackData.setUser(user);
        if (trackData.getCreatedAt() == null) {
            trackData.setCreatedAt(LocalDate.now());
        }
        return trackRepository.save(trackData);
    }
}