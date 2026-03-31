package taskmanager.task;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanager.exception.NotFoundException;
import taskmanager.project.Project;
import taskmanager.project.ProjectRepository;
import taskmanager.task.dto.CreateTaskRequest;
import taskmanager.task.dto.TaskResponse;
import taskmanager.task.filter.TaskFilter;
import taskmanager.task.specification.TaskSpecification;
import taskmanager.user.User;
import taskmanager.user.UserRepository;
import taskmanager.utils.TaskMapper;

import static taskmanager.exception.ResourceType.PROJECT;
import static taskmanager.exception.ResourceType.USER;

@Service
@AllArgsConstructor
public class TaskService
{
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Transactional
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

    @Transactional(readOnly = true)
    public Page<TaskResponse> findTasks(TaskFilter filter, Pageable pageable)
    {
        Specification<Task> specification = TaskSpecification.withFilter(filter);

        return taskRepository
                .findAll(specification, pageable)
                .map(taskMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> findTasks(Long id, Pageable pageable)
    {
        if (!projectRepository.existsById(id))
        {
            throw new NotFoundException(id, PROJECT);
        }

        return taskRepository.findByProjectId(id, pageable)
                        .map(taskMapper::toResponse);
    }
}