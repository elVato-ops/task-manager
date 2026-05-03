package taskmanager.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Project data used to create a new project")
public record CreateProjectRequest(
        @Schema(description = "Project name", example = "Custom project")
        @NotBlank
        String name)
{
}
