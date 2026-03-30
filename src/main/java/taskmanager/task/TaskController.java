package taskmanager.task;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taskmanager.PageResponse;
import taskmanager.task.dto.TaskResponse;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController
{
    private final TaskService taskService;

    @GetMapping
    public PageResponse<TaskResponse> getAll(Pageable pageable)
    {
        return new PageResponse<>(taskService.findAll(pageable));
    }
}
