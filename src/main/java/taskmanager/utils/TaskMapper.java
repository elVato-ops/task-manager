package taskmanager.utils;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import taskmanager.project.Project;
import taskmanager.task.Task;
import taskmanager.task.TaskStatus;
import taskmanager.task.dto.CreateTaskRequest;
import taskmanager.task.dto.TaskResponse;
import taskmanager.user.User;

import java.util.List;

@Component
@AllArgsConstructor
public class TaskMapper
{
    public Task toEntity(CreateTaskRequest request, Project project, User user)
    {
        return new Task(
                request.name(),
                TaskStatus.TODO,
                project,
                user);
    }

    public TaskResponse toResponse(Task task)
    {
        Long assigneeId = task.getAssignee() != null ? task.getAssignee().getId() : null;

        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getStatus(),
                task.getProject().getId(),
                assigneeId
                );
    }

    public List<TaskResponse> toResponse(List<Task> tasks)
    {
        return tasks.stream()
                .map(this::toResponse)
                .toList();
    }
}
