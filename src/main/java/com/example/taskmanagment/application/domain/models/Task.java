package com.example.taskmanagment.application.domain.models;

import com.example.taskmanagment.application.domain.enums.PriorityLevel;
import com.example.taskmanagment.application.domain.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@Table(name = "tasks")
public class Task {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String title;

    @Column(length = 2048)
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private PriorityLevel priority;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;


    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "assignee_id", referencedColumnName = "id")
    private User assignee;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Comment> comments;


    public Task() {
        this.comments = new ArrayList<>();
    }
}
