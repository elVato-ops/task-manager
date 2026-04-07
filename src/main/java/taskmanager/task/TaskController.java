package taskmanager.task;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
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
            @RequestParam(required = false) @Positive Long projectId,
            Pageable pageable,
            Authentication authentication)
    {
        Long userId = (Long) authentication.getPrincipal();

        TaskFilter taskFilter = TaskFilter.builder()
                .name(name)
                .status(status)
                .assigneeId(userId)
                .projectId(projectId)
                .build();

        return new PageResponse<>(taskService.getTasks(taskFilter, pageable));
    }

    @PatchMapping("{id}/status/{status}")
    public TaskResponse updateStatus(
            @PathVariable @Positive Long id,
            @PathVariable @NotNull TaskStatus status)
    {
        return taskService.updateStatus(id, status);
    }
}