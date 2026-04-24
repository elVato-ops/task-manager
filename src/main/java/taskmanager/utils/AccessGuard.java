package taskmanager.utils;

import org.springframework.stereotype.Component;
import taskmanager.auth.dto.AuthenticatedUser;
import taskmanager.exception.ForbiddenAccessException;
import taskmanager.exception.ResourceType;

@Component
public class AccessGuard
{
    public void verifyRights(AuthenticatedUser user,
                             Long ownerId,
                             ResourceType resourceType,
                             Long resourceId)
    {
        Long requesterId = user.id();
        if (!user.isAdmin() && !requesterId.equals(ownerId))
        {
            throw new ForbiddenAccessException(requesterId, resourceType, resourceId);
        }
    }
}