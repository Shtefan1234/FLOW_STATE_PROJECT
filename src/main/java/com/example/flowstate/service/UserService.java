package com.example.flowstate.service;
import com.example.flowstate.model.User;
import com.example.flowstate.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository=userRepository;
    }
    public List<User> findAll(){
        return userRepository.findAll();
    }
    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }
    public User save(User user){
        return userRepository.save(user);
    }
    public boolean existsById(Long id){
        return userRepository.existsById(id);
    }
    public void deleteById(Long id){
        userRepository.deleteById(id);
    }
}
