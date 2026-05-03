package taskmanager.user;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import taskmanager.auth.dto.AuthenticatedUser;
import taskmanager.response.PageResponse;
import taskmanager.user.dto.CreateUserRequest;
import taskmanager.user.dto.UserResponse;
import taskmanager.user.filter.UserFilter;

import java.net.URI;
import java.time.Instant;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Users", description = "User operations")
public class UserController
{
    private final UserService userService;

    @Operation(summary = "Create a new account user",
            description = "Creates a new user based on request. Fails if name, role or password is empty.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter missing",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Name already used",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request)
    {
        UserResponse user = userService.createUser(request);

        return ResponseEntity
                .created(URI.create("/users/" + user.id()))
                .body(user);
    }

    @Operation(summary = "Return user with a given id",
            description = "Returns a user with id specified in path. Fails if the user does not exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid id format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No access rights",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public UserResponse getUserById(
            @Parameter(description = "Id of the user")
            @PathVariable
            @Positive
            Long id,

            @AuthenticationPrincipal
            AuthenticatedUser user)
    {
        return userService.getUser(id, user);
    }


    @Operation(summary = "Return all users",
            description = "Returns all existing users. Fails if creation date is in the future")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of accounts",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PageableAsQueryParam
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> getUsers(
            @Parameter(description = "Name of the user")
            @RequestParam(required = false)
            String name,

            @Parameter(description = "User creation date")
            @RequestParam(required = false)
            @Past
            Instant fromCreationDate,

            @Parameter(description = "User role")
            @RequestParam(required = false)
            UserRole userRole,

            Pageable pageable)
    {
        UserFilter filter = UserFilter.builder()
                .name(name)
                .fromCreationDate(fromCreationDate)
                .userRole(userRole)
                .build();

        return new PageResponse<>(userService.getUsers(filter, pageable));
    }
}