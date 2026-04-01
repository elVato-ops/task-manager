package taskmanager.project;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import taskmanager.exception.NotFoundException;

import static taskmanager.exception.ResourceType.PROJECT;

@Component
@AllArgsConstructor
public class ProjectFinder
{
    private final ProjectRepository projectRepository;

    public Project getProject(Long id)
    {
        return projectRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(id, PROJECT));
    }

    public Page<Project> getProjects(Specification<Project> specification, Pageable pageable)
    {
        return projectRepository.findAll(specification, pageable);
    }

    public boolean existsById(Long id)
    {
        return projectRepository.existsById(id);
    }
}
