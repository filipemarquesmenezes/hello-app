package com.example.helloapp.welcome;

import com.example.helloapp.task.Task;
import com.example.helloapp.task.TaskRepository;
import com.example.helloapp.task.TaskStatus;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
public class WelcomeController {

    private final TaskRepository taskRepository;

    public WelcomeController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping({"/", "/welcome.html", "/login"})
    public String welcome(HttpServletResponse response, @AuthenticationPrincipal OAuth2User user) {
        response.setHeader("Cache-Control", "no-store");
        return user == null ? "welcome" : "redirect:/landing.html";
    }

    @GetMapping("/landing.html")
    public String landing(Model model, HttpServletResponse response,
                          @AuthenticationPrincipal OAuth2User user) {
        response.setHeader("Cache-Control", "no-store");
        model.addAttribute("githubLogin", user.getAttribute("login"));
        model.addAttribute("displayName", user.getAttribute("name"));
        model.addAttribute("githubId", user.getAttribute("id"));
        model.addAttribute("email", user.getAttribute("email"));
        model.addAttribute("avatarUrl", user.getAttribute("avatar_url"));

        List<Task> tasks = taskRepository.findAll().stream()
                .sorted(Comparator.comparing(Task::status).thenComparing(Task::description))
                .toList();
        model.addAttribute("tasks", tasks);
        model.addAttribute("statuses", TaskStatus.values());
        return "landing";
    }
}
