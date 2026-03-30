package taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

import static taskmanager.exception.ErrorCode.DATA_INVALID;
import static taskmanager.exception.ErrorCode.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e)
    {
        return response(e.getMessage(), e.getResource(), DATA_INVALID);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e)
    {
        return response(e.getMessage(), e.getResource(), NOT_FOUND);
    }

    private static ErrorResponse response(String message, ResourceType resource, ErrorCode errorCode)
    {
        return new ErrorResponse(message, resource, errorCode, Instant.now());
    }
}