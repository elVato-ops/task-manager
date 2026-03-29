package taskmanager.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ValidationException extends RuntimeException
{
    private final String message;
    private final ErrorCode errorCode;
}