package taskmanager.project;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanager.exception.NotFoundException;
import taskmanager.project.dto.CreateProjectRequest;
import taskmanager.project.dto.ProjectResponse;
import taskmanager.user.UserRepository;
import taskmanager.user.User;
import taskmanager.utils.ProjectMapper;

import static taskmanager.exception.ResourceType.PROJECT;
import static taskmanager.exception.ResourceType.USER;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ProjectService
{
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    public ProjectResponse createProject(@Valid CreateProjectRequest request)
    {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException(request.userId(), USER));

        return projectMapper.toResponse(
                projectRepository
                        .save(projectMapper.toEntity(request, user)));
    }

    public ProjectResponse getById(Long id)
    {
        return projectMapper.toResponse(
                projectRepository
                        .findById(id)
                        .orElseThrow(() -> new NotFoundException(id, PROJECT)));
    }
}