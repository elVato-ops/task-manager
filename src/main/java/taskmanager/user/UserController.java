package taskmanager.user;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskmanager.user.dto.CreateUserRequest;
import taskmanager.user.dto.UserResponse;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id)
    {
        return ResponseEntity
                .ok(userService.getUser(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers()
    {
        return ResponseEntity
                .ok(userService.getUsers());
    }
}