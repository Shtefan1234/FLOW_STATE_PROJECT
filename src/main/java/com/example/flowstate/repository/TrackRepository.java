package com.example.flowstate.repository;

import com.example.flowstate.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrackRepository extends JpaRepository<Track,Long> {
    List<Track> findByUserId(Long userId);
}
