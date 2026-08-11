package com.example.flowstate.service;

import com.example.flowstate.exception.TrackNotFoundException;
import com.example.flowstate.exception.UserNotFoundException;
import com.example.flowstate.model.Track;
import com.example.flowstate.model.User;
import com.example.flowstate.repository.TrackRepository;
import com.example.flowstate.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
}