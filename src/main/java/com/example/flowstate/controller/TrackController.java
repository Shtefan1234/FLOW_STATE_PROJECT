package com.example.flowstate.controller;
import com.example.flowstate.service.TrackService;
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
    private final TrackService trackService;
    public TrackController(TrackService trackService){
        this.trackService=trackService;
    }
    @GetMapping
    public List<Track> getAll(){
        return trackService.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Track> getById(@PathVariable Long id){
        Track track = trackService.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(track);
    }
    @PostMapping
    public ResponseEntity<Track> create(@RequestBody Track track){
        Track saved = trackService.save(track);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Track> update(@PathVariable Long id,@RequestBody Track track){
        if(!trackService.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        track.setId(id);
        return ResponseEntity.ok(trackService.save(track));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        if(!trackService.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        trackService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/api/users/{id}/tracks")
    public ResponseEntity<Track> createForUser(@PathVariable Long id , @RequestBody Track track){
        Track saved = trackService.createTrackForUser(id,track);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
