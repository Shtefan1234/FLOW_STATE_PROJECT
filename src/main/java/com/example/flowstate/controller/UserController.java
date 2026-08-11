package com.example.flowstate.controller;

import com.example.flowstate.dto.request.TrackRequest;
import com.example.flowstate.dto.request.UserRequest;
import com.example.flowstate.dto.response.TrackResponse;
import com.example.flowstate.dto.response.UserResponse;
import com.example.flowstate.mapper.TrackWebMapper;
import com.example.flowstate.mapper.UserWebMapper;
import com.example.flowstate.model.Track;
import com.example.flowstate.model.User;
import com.example.flowstate.service.TrackService;
import com.example.flowstate.service.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final TrackService trackService;
    private final UserWebMapper userWebMapper;
    private final TrackWebMapper trackWebMapper;

    @GetMapping
    public Page<UserResponse> getAll(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.findAll(pageable).map(userWebMapper::toResponse);
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable("id") Long userId) {
        return userWebMapper.toResponse(userService.getById(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        User saved = userService.save(userWebMapper.toEntity(request));
        return userWebMapper.toResponse(saved);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable("id") Long userId, @Valid @RequestBody UserRequest request) {
        User updated = userService.update(userId, userWebMapper.toEntity(request));
        return userWebMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long userId) {
        userService.delete(userId);
    }

    @PostMapping("/{id}/tracks")
    @ResponseStatus(HttpStatus.CREATED)
    public TrackResponse createTrackForUser(@PathVariable("id") Long userId, @Valid @RequestBody TrackRequest request) {
        Track saved = trackService.createTrackForUser(userId, trackWebMapper.toEntity(request));
        return trackWebMapper.toResponse(saved);
    }
}