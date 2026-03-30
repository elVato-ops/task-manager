package taskmanager.task.filter;

import lombok.Builder;
import lombok.Getter;
import taskmanager.task.TaskStatus;

@Builder
@Getter
public class TaskFilter
{
    private final String name;
    private final TaskStatus status;
    private final Long projectId;
    private final Long assigneeId;
}