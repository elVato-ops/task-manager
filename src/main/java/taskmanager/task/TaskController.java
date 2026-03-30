package taskmanager.task;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taskmanager.task.dto.TaskResponse;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController
{
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAll()
    {
        return ResponseEntity.ok(taskService.findAll());
    }
}
