package com.example.helloapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class TaskController {

    private final TaskRepository repository;

    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/tasks")
    public String create(@RequestParam String description) {
        repository.save(new Task(UUID.randomUUID(), description.strip(), TaskStatus.PENDING));
        return "redirect:/landing.html";
    }

    @PostMapping("/tasks/{id}/status")
    public String updateStatus(@PathVariable UUID id, @RequestParam TaskStatus status) {
        repository.findById(id).ifPresent(task -> {
            task.setStatus(status);
            repository.save(task);
        });
        return "redirect:/landing.html";
    }

    @PostMapping("/tasks/{id}/delete")
    public String delete(@PathVariable UUID id) {
        repository.deleteById(id);
        return "redirect:/landing.html";
    }
}
