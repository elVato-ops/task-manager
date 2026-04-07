package taskmanager.user.dto;

import taskmanager.user.UserRole;

public record UserResponse(Long id, String name, UserRole role)
{
}