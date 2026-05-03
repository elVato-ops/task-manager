package taskmanager.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import taskmanager.user.UserRole;

@Schema(description = "User data returned after a successful operation")
public record UserResponse(
        @Schema(description = "User id", example = "123")
        Long id,

        @Schema(description = "User name", example = "Bobek")
        String name,

        @Schema(description = "User role", example = "PROJECT_MANAGER")
        UserRole role)
{
}