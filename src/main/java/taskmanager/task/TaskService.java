package taskmanager.task;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import taskmanager.exception.NotFoundException;
import taskmanager.project.Project;
import taskmanager.project.ProjectRepository;
import taskmanager.task.dto.CreateTaskRequest;
import taskmanager.task.dto.TaskResponse;
import taskmanager.task.filter.TaskFilter;
import taskmanager.task.specification.TaskSpecifications;
import taskmanager.user.User;
import taskmanager.user.UserRepository;
import taskmanager.utils.TaskMapper;

import static taskmanager.exception.ResourceType.PROJECT;
import static taskmanager.exception.ResourceType.USER;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
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

    public Page<TaskResponse> findTasks(TaskFilter filter, Pageable pageable)
    {
        Specification<Task> spec = Specification.allOf();

        if (StringUtils.hasText(filter.getName()))
        {
            spec.and(TaskSpecifications.hasName(filter.getName()));
        }

        if (filter.getStatus() != null)
        {
            spec.and(TaskSpecifications.hasStatus(filter.getStatus()));
        }

        if (filter.getAssigneeId() != null)
        {
            spec.and(TaskSpecifications.isForAssignee(filter.getAssigneeId()));
        }

        if (filter.getProjectId() != null)
        {
            spec.and(TaskSpecifications.isForProject(filter.getProjectId()));
        }

        return taskRepository
                .findAll(spec, pageable)
                .map(taskMapper::toResponse);
    }

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