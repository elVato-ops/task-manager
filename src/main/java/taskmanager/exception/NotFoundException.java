package taskmanager.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException
{
    private final ResourceType resource;

    public NotFoundException(Long id, ResourceType resource)
    {
        super(resource + " with id " + id + " not found");

        this.resource = resource;
    }
}