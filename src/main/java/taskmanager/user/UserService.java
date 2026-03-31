package taskmanager.user;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanager.exception.NotFoundException;
import taskmanager.user.dto.CreateUserRequest;
import taskmanager.user.dto.UserResponse;
import taskmanager.user.filter.UserFilter;
import taskmanager.user.specification.UserSpecification;
import taskmanager.utils.UserMapper;

import static taskmanager.exception.ResourceType.USER;

@Service
@AllArgsConstructor
@Transactional
public class UserService
{
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Transactional
    public UserResponse createUser(CreateUserRequest request)
    {
        User user = userRepository.save(mapper.toEntity(request));
        return mapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(UserFilter filter, Pageable pageable)
    {
        Specification<User> specification = UserSpecification.withFilter(filter);

        return userRepository
                .findAll(specification, pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id)
    {
        return mapper.toResponse(
                userRepository
                        .findById(id)
                        .orElseThrow(() -> new NotFoundException(id, USER)));
    }
}