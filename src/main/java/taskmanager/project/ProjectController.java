package taskmanager.project;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import taskmanager.project.dto.CreateProjectRequest;
import taskmanager.project.dto.ProjectResponse;
import taskmanager.project.filter.ProjectFilter;
import taskmanager.response.PageResponse;
import taskmanager.task.TaskService;
import taskmanager.task.dto.CreateTaskRequest;
import taskmanager.task.dto.TaskResponse;

import java.net.URI;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController
{
    private final ProjectService projectService;
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            @Valid @RequestBody CreateProjectRequest request,
            Authentication authentication)
    {
        Long userId = (Long) authentication.getPrincipal();

        ProjectResponse project = projectService.createProject(request, userId);

        return ResponseEntity
                .created(URI.create("/projects/" + project.id()))
                .body(project);
    }

    @GetMapping
    public PageResponse<ProjectResponse> getAll(
            @RequestParam(required = false) String name,
            Pageable pageable,
            Authentication authentication)
    {
        Long userId = (Long) authentication.getPrincipal();

        ProjectFilter filter = ProjectFilter.builder()
                .name(name)
                .ownerId(userId)
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
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication)
    {
        Long userId = (Long) authentication.getPrincipal();

        TaskResponse task = taskService.createTask(request, id, userId);

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
