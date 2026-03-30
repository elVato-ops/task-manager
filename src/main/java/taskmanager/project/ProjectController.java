package taskmanager.project;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskmanager.project.dto.CreateProjectRequest;
import taskmanager.project.dto.ProjectResponse;
import taskmanager.task.TaskService;
import taskmanager.task.dto.CreateTaskRequest;
import taskmanager.task.dto.TaskResponse;

import java.net.URI;

@RestController
@RequestMapping("/projects")
@AllArgsConstructor
public class ProjectController
{
    private final ProjectService projectService;
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest request)
    {
        ProjectResponse project = projectService.createProject(request);

        return ResponseEntity
                .created(URI.create("/projects/" + project.id()))
                .body(project);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProjectResponse> getById(@PathVariable @Positive Long id)
    {
        return ResponseEntity
                .ok(projectService.getById(id));
    }

    @PostMapping("{projectId}/tasks")
    public ResponseEntity<TaskResponse> createTask(@PathVariable @Positive Long projectId, @Valid @RequestBody CreateTaskRequest request)
    {
        TaskResponse task = taskService.createTask(request, projectId);

        return ResponseEntity
                .created(URI.create("/projects/" + projectId + "/tasks/" + task.id()))
                .body(task);
    }
}
