package taskmanager.exception;

import lombok.Getter;
import taskmanager.user.UserRole;

@Getter
public class ForbiddenAccessException extends RuntimeException
{
    private final Long userId;
    private final UserRole userRole;

    public ForbiddenAccessException(Long userId, UserRole userRole)
    {
        super("User " + userId + " has no " + userRole + " rights");

        this.userId = userId;
        this.userRole = userRole;
    }
}
