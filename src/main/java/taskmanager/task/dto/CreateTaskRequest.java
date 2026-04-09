package taskmanager.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateTaskRequest(
        @NotBlank String name,
        @Positive Long assigneeId)
{
}