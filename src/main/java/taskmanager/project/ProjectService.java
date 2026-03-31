package taskmanager.project;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanager.exception.NotFoundException;
import taskmanager.project.dto.CreateProjectRequest;
import taskmanager.project.dto.ProjectResponse;
import taskmanager.project.filter.ProjectFilter;
import taskmanager.project.specification.ProjectSpecification;
import taskmanager.user.User;
import taskmanager.user.UserFinder;
import taskmanager.utils.ProjectMapper;

import static taskmanager.exception.ResourceType.PROJECT;

@Service
@AllArgsConstructor
public class ProjectService
{
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserFinder userFinder;

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request)
    {
        User user = userFinder.getUser(request.userId());

        return projectMapper.toResponse(
                projectRepository
                        .save(projectMapper.toEntity(request, user)));
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(Long id)
    {
        return projectMapper.toResponse(
                projectRepository
                        .findById(id)
                        .orElseThrow(() -> new NotFoundException(id, PROJECT)));
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getAll(ProjectFilter filter, Pageable pageable)
    {
        Specification<Project> specification = ProjectSpecification.withFilter(filter);

        return projectRepository
                .findAll(specification, pageable)
                .map(projectMapper::toResponse);
    }
}