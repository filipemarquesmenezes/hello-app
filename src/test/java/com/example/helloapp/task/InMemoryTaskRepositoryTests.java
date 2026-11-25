package com.example.helloapp.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskRepositoryTests {

    private InMemoryTaskRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTaskRepository();
    }

    @Test
    void saveAndFindById() {
        Task task = new Task(UUID.randomUUID(), "Write tests", TaskStatus.PENDING);
        repository.save(task);

        Optional<Task> found = repository.findById(task.id());

        assertTrue(found.isPresent());
        assertEquals(task, found.get());
        assertEquals("Write tests", found.get().description());
        assertEquals(TaskStatus.PENDING, found.get().status());
    }

    @Test
    void findByIdReturnsEmptyForUnknownId() {
        Optional<Task> found = repository.findById(UUID.randomUUID());
        assertTrue(found.isEmpty());
    }

    @Test
    void findAllReturnsAllSavedTasks() {
        Task a = new Task(UUID.randomUUID(), "Task A", TaskStatus.PENDING);
        Task b = new Task(UUID.randomUUID(), "Task B", TaskStatus.IN_PROGRESS);
        repository.save(a);
        repository.save(b);

        List<Task> all = repository.findAll();

        assertEquals(2, all.size());
        assertTrue(all.containsAll(List.of(a, b)));
    }

    @Test
    void findAllReturnsEmptyWhenNoTasks() {
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void saveOverwritesExistingTask() {
        UUID id = UUID.randomUUID();
        Task original = new Task(id, "Original", TaskStatus.PENDING);
        repository.save(original);

        original.setDescription("Updated");
        original.setStatus(TaskStatus.DONE);
        repository.save(original);

        Optional<Task> found = repository.findById(id);
        assertTrue(found.isPresent());
        assertEquals("Updated", found.get().description());
        assertEquals(TaskStatus.DONE, found.get().status());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void deleteByIdRemovesTask() {
        Task task = new Task(UUID.randomUUID(), "To delete", TaskStatus.PENDING);
        repository.save(task);

        repository.deleteById(task.id());

        assertTrue(repository.findById(task.id()).isEmpty());
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void deleteByIdForUnknownIdDoesNothing() {
        Task task = new Task(UUID.randomUUID(), "Keep me", TaskStatus.PENDING);
        repository.save(task);

        repository.deleteById(UUID.randomUUID());

        assertEquals(1, repository.findAll().size());
    }
}
