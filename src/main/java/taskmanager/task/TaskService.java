package taskmanager.task;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanager.auth.dto.AuthenticatedUser;
import taskmanager.exception.NotFoundException;
import taskmanager.project.Project;
import taskmanager.project.ProjectFinder;
import taskmanager.task.dto.CreateTaskRequest;
import taskmanager.task.dto.TaskResponse;
import taskmanager.task.filter.TaskFilter;
import taskmanager.task.mapper.TaskMapper;
import taskmanager.task.specification.TaskSpecification;
import taskmanager.user.User;
import taskmanager.user.UserFinder;

import static taskmanager.exception.ResourceType.PROJECT;
import static taskmanager.exception.ResourceType.TASK;
import static taskmanager.utils.AccessGuard.verifyRights;

@Service
@RequiredArgsConstructor
public class TaskService
{
    private final TaskRepository taskRepository;
    private final UserFinder userFinder;
    private final ProjectFinder projectFinder;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request, Long projectId, AuthenticatedUser currentUser)
    {
        Project project = projectFinder.getProject(projectId);
        verifyRights(currentUser, project.getOwner().getId(), PROJECT, projectId);

        User user = userFinder.getUser(request.assigneeId());

        return taskMapper.toResponse(
                taskRepository
                        .save(taskMapper.toEntity(request, project, user)));
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasks(TaskFilter filter, AuthenticatedUser authenticatedUser, Pageable pageable)
    {
        if (filter.getAssigneeId() != null)
        {
            verifyRights(authenticatedUser, filter.getAssigneeId(), TASK, null);
        }

        Specification<Task> specification = TaskSpecification.withFilter(filter);

        return taskRepository
                .findAll(specification, pageable)
                .map(taskMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasks(Long projectId, AuthenticatedUser currentUser, Pageable pageable)
    {
        Project project = projectFinder.getProject(projectId);
        verifyRights(currentUser, project.getOwner().getId(), PROJECT, projectId);

        return taskRepository.findByProjectId(projectId, pageable)
                        .map(taskMapper::toResponse);
    }

    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatus status, AuthenticatedUser authenticatedUser)
    {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, TASK));

        Long projectOwnerId = task.getProject().getOwner().getId();
        Long projectId = task.getProject().getId();
        verifyRights(authenticatedUser, projectOwnerId, PROJECT, projectId);

        task.updateStatus(status);

        return taskMapper.toResponse(
                taskRepository.save(task));
    }
}