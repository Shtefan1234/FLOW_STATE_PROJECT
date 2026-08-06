package com.example.flowstate.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.flowstate.model.Track;
import com.example.flowstate.repository.TrackRepository;

import java.util.List;
@RestController
@RequestMapping("/api/tracks")
public class TrackController {
    private final TrackRepository trackRepository;

    public TrackController(TrackRepository trackRepository){
        this.trackRepository=trackRepository;
    }
    @GetMapping
    public List<Track> getAll(){
        return trackRepository.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Track> getById(@PathVariable Long id){
        Track track = trackRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(track);
    }
    @PostMapping
    public ResponseEntity<Track> create(@RequestBody Track track){
        Track saved = trackRepository.save(track);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Track> update(@PathVariable Long id,@RequestBody Track track){
        if(!trackRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        track.setId(id);
        return ResponseEntity.ok(trackRepository.save(track));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        if(!trackRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        trackRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
