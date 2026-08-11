package com.example.flowstate.repository;

import com.example.flowstate.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.flowstate.model.TrackCategory;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
public interface TrackRepository extends JpaRepository<Track,Long> {
    List<Track> findByUserId(Long userId);
    Page<Track> findByCategory(TrackCategory category, Pageable pageable);
}
