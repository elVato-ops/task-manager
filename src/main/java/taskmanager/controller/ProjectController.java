package taskmanager.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taskmanager.service.ProjectService;

@RestController
@RequestMapping("/projects")
@AllArgsConstructor
public class ProjectController
{
    private final ProjectService projectService;
}
