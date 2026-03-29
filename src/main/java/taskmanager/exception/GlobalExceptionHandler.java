package taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e)
    {
        return response(e.getMessage(), e.getErrorCode());
    }

    private static ErrorResponse response(String message, ErrorCode errorCode)
    {
        return new ErrorResponse(message, errorCode, Instant.now());
    }
}