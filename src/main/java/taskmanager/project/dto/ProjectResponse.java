package taskmanager.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Project data returned after a successful operation")
public record ProjectResponse(
        @Schema(description = "Project id", example = "355")
        Long id,

        @Schema(description = "Project name", example = "Custom Project")
        String name,

        @Schema(description = "Project owner id", example = "123")
        Long ownerId)
{
}