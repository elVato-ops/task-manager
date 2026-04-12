package taskmanager.exception;

import lombok.Getter;

@Getter
public class ForbiddenAccessException extends RuntimeException
{
    private final Long requesterId;
    private final ResourceType resourceType;
    private final Long resourceId;

    public ForbiddenAccessException(Long requesterId, ResourceType resourceType, Long resourceId)
    {
        super("User " + requesterId + " has no access to " + resourceType + " " + resourceId);

        this.requesterId = requesterId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
}
