package com.example.helloapp.task;

import java.util.Objects;
import java.util.UUID;

public class Task {

    private final UUID id;
    private String description;
    private TaskStatus status;

    public Task(UUID id, String description, TaskStatus status) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(status, "status must not be null");
        this.id = id;
        this.description = description;
        this.status = status;
    }

    public UUID id() {
        return id;
    }

    public String description() {
        return description;
    }

    public void setDescription(String description) {
        Objects.requireNonNull(description, "description must not be null");
        this.description = description;
    }

    public TaskStatus status() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Task other && id.equals(other.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Task[id=%s, description=%s, status=%s]".formatted(id, description, status);
    }
}
