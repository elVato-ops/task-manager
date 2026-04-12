package taskmanager.task;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import taskmanager.auth.dto.AuthenticatedUser;
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
            @RequestParam(required = false) @Positive Long assigneeId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            Pageable pageable)
    {
        TaskFilter taskFilter = TaskFilter.builder()
                .name(name)
                .status(status)
                .assigneeId(assigneeId)
                .projectId(projectId)
                .build();

        return new PageResponse<>(taskService.getTasks(taskFilter, authenticatedUser, pageable));
    }

    @PatchMapping("{id}/status/{status}")
    public TaskResponse updateStatus(
            @PathVariable @Positive Long id,
            @PathVariable @NotNull TaskStatus status,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser)
    {
        return taskService.updateStatus(id, status, authenticatedUser);
    }
}