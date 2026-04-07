package taskmanager.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import taskmanager.user.User;
import taskmanager.user.dto.CreateUserRequest;
import taskmanager.user.dto.UserResponse;

@Component
@RequiredArgsConstructor
public class UserMapper
{
    private final PasswordEncoder passwordEncoder;

    public User toEntity(CreateUserRequest request)
    {
        return new User(request.name(), request.role(), passwordEncoder.encode(request.password()));
    }

    public UserResponse toResponse(User user)
    {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getRole());
    }
}