package com.example.taskmanagment.application.ports.out;

import com.example.taskmanagment.application.domain.models.User;

import java.util.Optional;
import java.util.UUID;


public interface UserRepository {
    public void save(User user);
    public Optional<User> findByEmail(String email);

    public Optional<User> findById(UUID id);
}
