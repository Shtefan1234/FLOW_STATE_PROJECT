package com.example.flowstate.controller;
import com.example.flowstate.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.flowstate.model.User;
import com.example.flowstate.repository.UserRepository;
import java.util.List;
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){
        this.userService=userService;
    }
    @GetMapping
    public List<User> getAll(){
        return userService.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id){
        User user = userService.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(user);
    }
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        User saved = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id,@RequestBody User user){
        if(!userService.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        user.setId(id);
        return ResponseEntity.ok(userService.save(user));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        if(!userService.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
