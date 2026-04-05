package taskmanager.task.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTaskRequest(
        @NotBlank String name)
{
}