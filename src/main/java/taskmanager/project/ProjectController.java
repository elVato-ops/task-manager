package taskmanager.project;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import taskmanager.auth.dto.AuthenticatedUser;
import taskmanager.export.ProjectExportResponse;
import taskmanager.export.ProjectExportService;
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
    private final ProjectExportService exportService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ProjectResponse> create(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal Long currentUserId)
    {
        ProjectResponse project = projectService.createProject(request, currentUserId);

        return ResponseEntity
                .created(URI.create("/projects/" + project.id()))
                .body(project);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<ProjectResponse> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long ownerId,
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
            @PathVariable @Positive Long id,
            @AuthenticationPrincipal AuthenticatedUser user)
    {
        return projectService.getProject(id, user);
    }

    @PostMapping("{id}/tasks")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable @Positive Long id,
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal AuthenticatedUser user)
    {
        TaskResponse task = taskService.createTask(request, id, user);

        return ResponseEntity
                .created(URI.create("/projects/" + id + "/tasks/" + task.id()))
                .body(task);
    }

    @GetMapping("{id}/tasks")
    public PageResponse<TaskResponse> getTasks(
            @PathVariable @Positive Long id,
            @AuthenticationPrincipal AuthenticatedUser user,
            Pageable pageable)
    {
        Page<TaskResponse> page = taskService.getTasks(id, user, pageable);
        return new PageResponse<>(page);
    }

    @PostMapping("{id}/export")
    public ProjectExportResponse exportDataForProject(
            @PathVariable @Positive Long id)
    {
        return exportService.exportData(id);
    }
}