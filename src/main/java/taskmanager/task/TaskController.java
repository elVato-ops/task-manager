package taskmanager.task;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
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

    @Operation(summary = "Return all tasks",
            description = "Returns all existing tasks.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of tasks",
                    content = @Content(schema = @Schema(implementation = PageResponse.class)))
    })
    @PageableAsQueryParam
    @GetMapping
    public PageResponse<TaskResponse> getAll(
            @Parameter(description = "Task name")
            @RequestParam(required = false)
            String name,

            @Parameter(description = "Task status")
            @RequestParam(required = false)
            TaskStatus status,

            @Parameter(description = "Id of the task's project")
            @RequestParam(required = false)
            @Positive
            Long projectId,

            @Parameter(description = "Id of the task's assignee")
            @RequestParam(required = false)
            @Positive
            Long assigneeId,

            @AuthenticationPrincipal
            AuthenticatedUser authenticatedUser,

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

    @Operation(summary = "Updates status of a given task",
            description = "Updates status of a given task. Fails if the task does not exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated task",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("{id}/status/{status}")
    public TaskResponse updateStatus(
            @PathVariable
            @Positive
            Long id,

            @PathVariable
            @NotNull
            TaskStatus status,

            @AuthenticationPrincipal
            AuthenticatedUser authenticatedUser)
    {
        return taskService.updateStatus(id, status, authenticatedUser);
    }
}