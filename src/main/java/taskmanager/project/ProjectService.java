package taskmanager.project;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanager.project.dto.CreateProjectRequest;
import taskmanager.project.dto.ProjectResponse;
import taskmanager.project.filter.ProjectFilter;
import taskmanager.project.specification.ProjectSpecification;
import taskmanager.user.User;
import taskmanager.user.UserFinder;
import taskmanager.utils.ProjectMapper;

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
    public ProjectResponse getProject(Long id)
    {
        return projectMapper.toResponse(
                projectFinder.getProject(id));
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