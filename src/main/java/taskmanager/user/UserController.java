package taskmanager.user;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import taskmanager.response.PageResponse;
import taskmanager.user.dto.CreateUserRequest;
import taskmanager.user.dto.UserResponse;
import taskmanager.user.filter.UserFilter;

import java.net.URI;
import java.time.Instant;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Validated
public class UserController
{
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request)
    {
        UserResponse user = userService.createUser(request);

        return ResponseEntity
                .created(URI.create("/users/" + user.id()))
                .body(user);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(
            @PathVariable @Positive Long id)
    {
        return userService.getUser(id);
    }

    @GetMapping
    public PageResponse<UserResponse> getUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @Past Instant fromCreationDate,
            Pageable pageable)
    {
        UserFilter filter = UserFilter.builder()
                .name(name)
                .fromCreationDate(fromCreationDate)
                .build();

        return new PageResponse<>(userService.getUsers(filter, pageable));
    }
}