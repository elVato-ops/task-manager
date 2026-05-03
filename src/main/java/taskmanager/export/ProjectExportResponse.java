package taskmanager.export;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Project export data returned after a successful operation")
public record ProjectExportResponse(
        @Schema(description = "Project id", example = "123")
        Long projectId)
{
}
