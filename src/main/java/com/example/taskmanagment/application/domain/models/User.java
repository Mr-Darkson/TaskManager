package com.example.taskmanagment.application.domain.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    @Size(min = 3, max = 30)
    private String username;

    @Email
    @Size(max = 60)
    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Task> createdTasks;

    @OneToMany(mappedBy = "assignee", fetch = FetchType.LAZY)
    private List<Task> assignedTasks;


    public User() {
        this.createdTasks = new ArrayList<>();
        this.assignedTasks = new ArrayList<>();
    }
}
