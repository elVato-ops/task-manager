package taskmanager.exception;

import java.time.Instant;

public record ErrorResponse(String message, ErrorCode errorCode, Instant timestamp)
{
}
