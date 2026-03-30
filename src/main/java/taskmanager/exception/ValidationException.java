package taskmanager.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException
{
    private final ResourceType resource;

    public ValidationException(String message, ResourceType resource)
    {
        super(message);

        this.resource = resource;
    }
}