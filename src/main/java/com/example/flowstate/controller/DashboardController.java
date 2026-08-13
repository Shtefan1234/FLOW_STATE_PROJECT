package com.example.flowstate.controller;

import com.example.flowstate.model.Task;
import com.example.flowstate.model.TaskCategory;
import com.example.flowstate.model.TaskStatus;
import com.example.flowstate.model.Track;
import com.example.flowstate.model.User;
import com.example.flowstate.service.TaskService;
import com.example.flowstate.service.TrackService;
import com.example.flowstate.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DashboardController {

    private final UserService userService;
    private final TrackService trackService;
    private final TaskService taskService;

    public DashboardController(UserService userService, TrackService trackService, TaskService taskService) {
        this.userService = userService;
        this.trackService = trackService;
        this.taskService = taskService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        List<User> users = userService.findAll(Pageable.unpaged()).getContent();
        model.addAttribute("users", users);
        model.addAttribute("categories", TaskCategory.values());
        model.addAttribute("statuses", TaskStatus.values());
        return "dashboard";
    }

    @PostMapping("/users")
    public String createUser(@RequestParam String name, @RequestParam String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        userService.save(user);
        return "redirect:/";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/";
    }

    @PostMapping("/users/{id}/tracks")
    public String createTrack(@PathVariable Long id,
                              @RequestParam String title,
                              @RequestParam(required = false) String deadline) {
        Track track = new Track();
        track.setTitle(title);
        if (deadline != null && !deadline.isBlank()) {
            track.setDeadline(LocalDate.parse(deadline));
        }
        trackService.createTrackForUser(id, track);
        return "redirect:/";
    }

    @PostMapping("/tracks/{id}/delete")
    public String deleteTrack(@PathVariable Long id) {
        trackService.delete(id);
        return "redirect:/";
    }

    @PostMapping("/tracks/{trackId}/tasks")
    public String createTask(@PathVariable Long trackId,
                             @RequestParam String title,
                             @RequestParam String status,
                             @RequestParam String category,
                             @RequestParam String date,
                             @RequestParam int orderIndex) {
        Task task = new Task();
        task.setTitle(title);
        task.setStatus(TaskStatus.valueOf(status));
        task.setCategory(TaskCategory.valueOf(category));
        task.setDate(LocalDate.parse(date));
        task.setOrderIndex(orderIndex);
        taskService.createTaskForTrack(trackId, task);
        return "redirect:/";
    }

    @PostMapping("/tasks/{id}/toggle")
    public String toggleTask(@PathVariable Long id) {
        Task task = taskService.getById(id);
        task.setStatus(task.getStatus() == TaskStatus.DONE ? TaskStatus.PENDING : TaskStatus.DONE);
        taskService.save(task);
        return "redirect:/";
    }

    @PostMapping("/tasks/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        taskService.delete(id);
        return "redirect:/";
    }
}
