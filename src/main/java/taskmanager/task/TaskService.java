package taskmanager.task;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanager.exception.NotFoundException;
import taskmanager.project.Project;
import taskmanager.project.ProjectRepository;
import taskmanager.task.dto.CreateTaskRequest;
import taskmanager.task.dto.TaskResponse;
import taskmanager.user.User;
import taskmanager.user.UserRepository;
import taskmanager.utils.TaskMapper;

import java.util.List;

import static taskmanager.exception.ResourceType.PROJECT;
import static taskmanager.exception.ResourceType.USER;

@Service
@Transactional
@AllArgsConstructor
public class TaskService
{
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public TaskResponse createTask(CreateTaskRequest request, Long projectId)
    {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new NotFoundException(projectId, PROJECT));

        User user = null;
        if (request.userId() != null)
        {
            user = userRepository
                    .findById(request.userId())
                    .orElseThrow(() -> new NotFoundException(request.userId(), USER));
        }

        return taskMapper.toResponse(
                taskRepository
                        .save(taskMapper.toEntity(request, project, user)));
    }

    public List<TaskResponse> findAll()
    {
        return taskMapper.toResponse(
                taskRepository
                        .findAll());
    }
}