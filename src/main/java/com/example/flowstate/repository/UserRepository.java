package com.example.flowstate.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.flowstate.model.User;

public interface UserRepository extends JpaRepository<User,Long> {
    Page<User> findAll(Pageable pageable);
}
