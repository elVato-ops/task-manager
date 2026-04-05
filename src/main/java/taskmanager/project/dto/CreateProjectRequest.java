package taskmanager.project.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProjectRequest(
        @NotBlank String name)
{
}
