package taskmanager.project;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
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
@Tag(name = "Projects", description = "Project operations")
public class ProjectController
{
    private final ProjectService projectService;
    private final TaskService taskService;
    private final ProjectExportService exportService;

    @Operation(summary = "Create a new project",
            description = "Creates a new project based on request. Fails if name is empty or user does not exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Name missing",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No access rights",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User does not exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ProjectResponse> create(
            @Valid
            @RequestBody
            CreateProjectRequest request,

            @AuthenticationPrincipal
            Long currentUserId)
    {
        ProjectResponse project = projectService.createProject(request, currentUserId);

        return ResponseEntity
                .created(URI.create("/projects/" + project.id()))
                .body(project);
    }

    @Operation(summary = "Return all projects",
            description = "Returns all existing projects.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of projects",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "403", description = "No access rights",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    @PageableAsQueryParam
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<ProjectResponse> getAll(
            @Parameter(description = "Name of the project")
            @RequestParam(required = false)
            String name,

            @Parameter(description = "User id of the project owner")
            @RequestParam(required = false)
            Long ownerId,

            Pageable pageable)
    {
        ProjectFilter filter = ProjectFilter.builder()
                .name(name)
                .ownerId(ownerId)
                .build();

        return new PageResponse<>(projectService.getProjects(filter, pageable));
    }

    @Operation(summary = "Return project with a given id",
            description = "Returns a project with id specified in path. Fails if the project does not exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project found",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid id format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No access rights",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("{id}")
    public ProjectResponse getById(
            @Parameter(description = "Id of the project")
            @PathVariable
            @Positive Long id,

            @AuthenticationPrincipal
            AuthenticatedUser user)
    {
        return projectService.getProject(id, user);
    }

    @Operation(summary = "Create a new task",
            description = "Creates task for a given project. Fails if the project does not exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Request invalid, possible reasons include no name, status or project id.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No access rights",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project or user does not exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("{id}/tasks")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<TaskResponse> createTask(
            @Parameter(description = "Project id")
            @PathVariable
            @Positive
            Long id,

            @Valid
            @RequestBody
            CreateTaskRequest request,

            @AuthenticationPrincipal AuthenticatedUser user)
    {
        TaskResponse task = taskService.createTask(request, id, user);

        return ResponseEntity
                .created(URI.create("/projects/" + id + "/tasks/" + task.id()))
                .body(task);
    }

    @Operation(summary = "Return tasks from a given project",
            description = "Returns a list of tasks assigned to a given project. Fails if the project does not exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of tasks",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Project id format invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("{id}/tasks")
    @PageableAsQueryParam
    public PageResponse<TaskResponse> getTasks(
            @Parameter(description = "Project id")
            @PathVariable
            @Positive
            Long id,

            @AuthenticationPrincipal
            AuthenticatedUser user,

            Pageable pageable)
    {
        Page<TaskResponse> page = taskService.getTasks(id, user, pageable);
        return new PageResponse<>(page);
    }

    @Operation(summary = "Return a mocked report for a given project",
            description = "Returns a mocked report based on a project. Fails if the project does not exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project export",
                    content = @Content(schema = @Schema(implementation = ProjectExportResponse.class))),
            @ApiResponse(responseCode = "400", description = "Project id format invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Export generation not possible",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("{id}/export")
    public ProjectExportResponse exportDataForProject(
            @Parameter(description = "Project id")
            @PathVariable
            @Positive Long id)
    {
        return exportService.exportData(id);
    }
}