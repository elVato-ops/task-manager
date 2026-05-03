package taskmanager.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import taskmanager.task.TaskStatus;

@Schema(description = "Task data returned after a successful operation")
public record TaskResponse(
        @Schema(description = "Task id", example = "123")
        Long id,

        @Schema(description = "Task name", example = "Some task")
        String name,

        @Schema(description = "Task status", example = "IN_PROGRESS")
        TaskStatus status,

        @Schema(description = "Project id", example = "456")
        Long projectId,

        @Schema(description = "Task assignee id", example = "798")
        Long assigneeId)
{
}
