package taskmanager.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import taskmanager.user.UserRole;

public record CreateUserRequest(
        @NotBlank String name,
        @NotBlank String password,
        @NotNull UserRole role)
{
}