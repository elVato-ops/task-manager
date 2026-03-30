package taskmanager.utils;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getStatus(),
                task.getProject().getId(),
                task.getAssignee().getId());
    }

    public List<TaskResponse> toResponse(List<Task> tasks)
    {
        return tasks.stream()
                .map(this::toResponse)
                .toList();
    }
}
