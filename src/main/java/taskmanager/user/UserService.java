package taskmanager.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanager.exception.NotFoundException;
import taskmanager.user.dto.CreateUserRequest;
import taskmanager.user.dto.UserResponse;
import taskmanager.utils.UserMapper;

import java.util.List;

import static taskmanager.exception.ResourceType.USER;

@Service
@AllArgsConstructor
@Transactional
public class UserService
{
    private final UserRepository userRepository;
    private final UserMapper mapper;

    public UserResponse createUser(CreateUserRequest request)
    {
        User user = userRepository.save(mapper.toEntity(request));
        return mapper.toResponse(user);
    }

    public List<UserResponse> fetchUsers()
    {
        return mapper.toResponse(userRepository.findAll());
    }

    public UserResponse fetchUser(Long id)
    {
        return mapper.toResponse(
                userRepository
                        .findById(id)
                        .orElseThrow(() -> new NotFoundException(id, USER)));
    }
}