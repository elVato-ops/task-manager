package taskmanager.exception;

import lombok.Getter;

@Getter
public class ForbiddenAccessException extends RuntimeException
{
    public ForbiddenAccessException(String message)
    {
        super(message);
    }
}
