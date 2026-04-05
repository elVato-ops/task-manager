package taskmanager.utils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import taskmanager.project.Project;
import taskmanager.project.dto.CreateProjectRequest;
import taskmanager.project.dto.ProjectResponse;
import taskmanager.user.User;

@Component
@RequiredArgsConstructor
public class ProjectMapper
{
    public Project toEntity(@Valid CreateProjectRequest request, User user)
    {
        return new Project(
                request.name(),
                user
        );
    }

    public ProjectResponse toResponse(Project project)
    {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getOwner().getId());
    }
}
