package taskmanager.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import taskmanager.user.UserRole;

@Schema(description = "User data to create a new user")
public record CreateUserRequest(
        @Schema(description = "User name", example = "Bobek")
        @NotBlank String name,

        @Schema(description = "User name", example = "abc123#$@")
        @NotBlank String password,

        @Schema(description = "User role", example = "PROJECT_MANAGER")
        @NotNull UserRole role)
{
}