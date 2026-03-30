package taskmanager.user;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskmanager.PageResponse;
import taskmanager.user.dto.CreateUserRequest;
import taskmanager.user.dto.UserResponse;

import java.net.URI;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController
{
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request)
    {
        UserResponse user = userService.createUser(request);

        return ResponseEntity
                .created(URI.create("/users/" + user.id()))
                .body(user);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable @Positive Long id)
    {
        return userService.getUser(id);
    }

    @GetMapping
    public PageResponse<UserResponse> getUsers(Pageable pageable)
    {
        Page<UserResponse> users = userService.getUsers(pageable);
        return new PageResponse<>(users);
    }
}