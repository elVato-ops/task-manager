package taskmanager.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(description = "Task data used to create a new task")
public record CreateTaskRequest(
        @Schema(description = "Task name", example = "Some task")
        @NotBlank
        String name,

        @Schema(description = "Assigned user id", example = "123")
        @Positive
        Long assigneeId)
{
}