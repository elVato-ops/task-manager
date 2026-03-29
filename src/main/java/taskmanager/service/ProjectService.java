package taskmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanager.repository.ProjectRepository;

@Service
@Transactional
@AllArgsConstructor
public class ProjectService
{
    private final ProjectRepository projectRepository;
}
