package taskmanager.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException
{
    private final ResourceType resource;
    private final Long id;

    public NotFoundException(Long id, ResourceType resource)
    {
        super(resource + " with id " + id + " not found");

        this.resource = resource;
        this.id = id;
    }
}