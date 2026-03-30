package taskmanager.exception;

import java.time.Instant;

public record ErrorResponse(String message, ResourceType resource, ErrorCode errorCode, Instant timestamp)
{
}