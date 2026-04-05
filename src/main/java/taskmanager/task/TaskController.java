package taskmanager.task;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import taskmanager.response.PageResponse;
import taskmanager.task.dto.TaskResponse;
import taskmanager.task.filter.TaskFilter;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController
{
    private final TaskService taskService;

    @GetMapping
    public PageResponse<TaskResponse> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) @Positive Long assigneeId,
            @RequestParam(required = false) @Positive Long projectId,
            Pageable pageable)
    {
        TaskFilter taskFilter = TaskFilter.builder()
                .name(name)
                .status(status)
                .assigneeId(assigneeId)
                .projectId(projectId)
                .build();

        return new PageResponse<>(taskService.getTasks(taskFilter, pageable));
    }
}