package taskmanager.task.dto;

import taskmanager.task.TaskStatus;

public record TaskResponse(
        Long id,
        String name,
        TaskStatus status,
        Long projectId,
        Long assigneeId)
{
}
