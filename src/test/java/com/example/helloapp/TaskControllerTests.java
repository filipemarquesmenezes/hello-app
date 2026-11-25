package com.example.helloapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.security.oauth2.client.registration.github.client-id=test-client",
        "spring.security.oauth2.client.registration.github.client-secret=test-secret"
})
@AutoConfigureMockMvc
class TaskControllerTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.findAll().forEach(task -> taskRepository.deleteById(task.id()));
    }

    @Test
    void landingShowsEmptyTaskState() throws Exception {
        mvc.perform(get("/landing.html").with(oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tasks")))
                .andExpect(content().string(containsString("No tasks yet")));
    }

    @Test
    void createTaskRedirectsToLanding() throws Exception {
        mvc.perform(post("/tasks").with(oauth2Login()).with(csrf())
                        .param("description", "Buy groceries"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/landing.html"));

        mvc.perform(get("/landing.html").with(oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Buy groceries")))
                .andExpect(content().string(containsString("PENDING")));
    }

    @Test
    void updateTaskStatus() throws Exception {
        Task task = new Task(UUID.randomUUID(), "Read a book", TaskStatus.PENDING);
        taskRepository.save(task);

        mvc.perform(post("/tasks/" + task.id() + "/status").with(oauth2Login()).with(csrf())
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/landing.html"));

        mvc.perform(get("/landing.html").with(oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("IN PROGRESS")));
    }

    @Test
    void deleteTask() throws Exception {
        Task task = new Task(UUID.randomUUID(), "To be deleted", TaskStatus.PENDING);
        taskRepository.save(task);

        mvc.perform(post("/tasks/" + task.id() + "/delete").with(oauth2Login()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/landing.html"));

        mvc.perform(get("/landing.html").with(oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("To be deleted"))));
    }

    @Test
    void landingShowsTaskForm() throws Exception {
        mvc.perform(get("/landing.html").with(oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("action=\"/tasks\"")))
                .andExpect(content().string(containsString("What needs to be done?")));
    }
}
