package taskmanager.project;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanager.auth.dto.AuthenticatedUser;
import taskmanager.project.dto.CreateProjectRequest;
import taskmanager.project.dto.ProjectResponse;
import taskmanager.project.filter.ProjectFilter;
import taskmanager.project.mapper.ProjectMapper;
import taskmanager.project.specification.ProjectSpecification;
import taskmanager.user.User;
import taskmanager.user.UserFinder;

import static taskmanager.exception.ResourceType.PROJECT;
import static taskmanager.utils.AccessGuard.verifyRights;

@Service
@RequiredArgsConstructor
public class ProjectService
{
    private final ProjectRepository projectRepository;
    private final ProjectFinder projectFinder;
    private final ProjectMapper projectMapper;
    private final UserFinder userFinder;

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, Long userId)
    {
        User user = userFinder.getUser(userId);

        return projectMapper.toResponse(
                projectRepository
                        .save(projectMapper.toEntity(request, user)));
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(Long id, AuthenticatedUser user)
    {
        Project project = projectFinder.getProject(id);
        verifyRights(user, project.getOwner().getId(), PROJECT, project.getId());

        return projectMapper.toResponse(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getProjects(ProjectFilter filter, Pageable pageable)
    {
        Specification<Project> specification = ProjectSpecification.withFilter(filter);

        return projectFinder
                .getProjects(specification, pageable)
                .map(projectMapper::toResponse);
    }
}