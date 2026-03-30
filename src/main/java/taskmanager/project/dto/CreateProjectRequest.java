package taskmanager.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateProjectRequest(@NotBlank String name, @Positive Long userId)
{
}
