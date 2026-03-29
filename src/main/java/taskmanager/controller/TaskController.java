package taskmanager.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taskmanager.service.TaskService;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController
{
    private final TaskService taskService;
}
