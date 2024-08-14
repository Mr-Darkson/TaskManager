package com.example.taskmanagment.adapters.out.persistence;

import com.example.taskmanagment.application.domain.models.User;
import com.example.taskmanagment.application.ports.out.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<User, UUID> {
     Optional<User> findByEmail(String email);
     Optional<User> findById(UUID id);


}
