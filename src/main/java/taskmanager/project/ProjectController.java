package taskmanager.project;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import taskmanager.response.PageResponse;
import taskmanager.project.dto.CreateProjectRequest;
import taskmanager.project.dto.ProjectResponse;
import taskmanager.project.filter.ProjectFilter;
import taskmanager.task.TaskService;
import taskmanager.task.dto.CreateTaskRequest;
import taskmanager.task.dto.TaskResponse;

import java.net.URI;

@RestController
@RequestMapping("/projects")
@AllArgsConstructor
@Validated
public class ProjectController
{
    private final ProjectService projectService;
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            @Valid @RequestBody CreateProjectRequest request)
    {
        ProjectResponse project = projectService.createProject(request);

        return ResponseEntity
                .created(URI.create("/projects/" + project.id()))
                .body(project);
    }

    @GetMapping
    public PageResponse<ProjectResponse> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @Positive Long ownerId,
            Pageable pageable)
    {
        ProjectFilter filter = ProjectFilter.builder()
                .name(name)
                .ownerId(ownerId)
                .build();

        return new PageResponse<>(projectService.getProjects(filter, pageable));
    }

    @GetMapping("{id}")
    public ProjectResponse getById(
            @PathVariable @Positive Long id)
    {
        return projectService.getProject(id);
    }

    @PostMapping("{id}/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable @Positive Long id,
            @Valid @RequestBody CreateTaskRequest request)
    {
        TaskResponse task = taskService.createTask(request, id);

        return ResponseEntity
                .created(URI.create("/projects/" + id + "/tasks/" + task.id()))
                .body(task);
    }

    @GetMapping("{id}/tasks")
    public PageResponse<TaskResponse> getTasks(
            @PathVariable @Positive Long id,
            Pageable pageable)
    {
        Page<TaskResponse> page = taskService.getTasks(id, pageable);
        return new PageResponse<>(page);
    }
}
