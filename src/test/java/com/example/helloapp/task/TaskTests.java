package com.example.helloapp.task;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskTests {

    @Test
    void constructorSetsFields() {
        UUID id = UUID.randomUUID();
        Task task = new Task(id, "Do something", TaskStatus.PENDING);

        assertEquals(id, task.id());
        assertEquals("Do something", task.description());
        assertEquals(TaskStatus.PENDING, task.status());
    }

    @Test
    void setDescriptionUpdatesValue() {
        Task task = new Task(UUID.randomUUID(), "Old", TaskStatus.PENDING);
        task.setDescription("New");
        assertEquals("New", task.description());
    }

    @Test
    void setStatusUpdatesValue() {
        Task task = new Task(UUID.randomUUID(), "Task", TaskStatus.PENDING);
        task.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, task.status());
    }

    @Test
    void constructorRejectsNullId() {
        assertThrows(NullPointerException.class, () -> new Task(null, "desc", TaskStatus.PENDING));
    }

    @Test
    void constructorRejectsNullDescription() {
        assertThrows(NullPointerException.class, () -> new Task(UUID.randomUUID(), null, TaskStatus.PENDING));
    }

    @Test
    void constructorRejectsNullStatus() {
        assertThrows(NullPointerException.class, () -> new Task(UUID.randomUUID(), "desc", null));
    }

    @Test
    void setDescriptionRejectsNull() {
        Task task = new Task(UUID.randomUUID(), "desc", TaskStatus.PENDING);
        assertThrows(NullPointerException.class, () -> task.setDescription(null));
    }

    @Test
    void setStatusRejectsNull() {
        Task task = new Task(UUID.randomUUID(), "desc", TaskStatus.PENDING);
        assertThrows(NullPointerException.class, () -> task.setStatus(null));
    }

    @Test
    void equalsAndHashCodeBasedOnId() {
        UUID id = UUID.randomUUID();
        Task a = new Task(id, "A", TaskStatus.PENDING);
        Task b = new Task(id, "B", TaskStatus.DONE);
        Task c = new Task(UUID.randomUUID(), "A", TaskStatus.PENDING);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    void toStringContainsFields() {
        UUID id = UUID.randomUUID();
        Task task = new Task(id, "My task", TaskStatus.IN_PROGRESS);
        String str = task.toString();

        assertTrue(str.contains(id.toString()));
        assertTrue(str.contains("My task"));
        assertTrue(str.contains("IN_PROGRESS"));
    }
}
