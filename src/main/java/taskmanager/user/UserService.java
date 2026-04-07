package taskmanager.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanager.auth.AuthService;
import taskmanager.exception.NameInUseException;
import taskmanager.user.dto.CreateUserRequest;
import taskmanager.user.dto.UserResponse;
import taskmanager.user.filter.UserFilter;
import taskmanager.user.specification.UserSpecification;
import taskmanager.utils.UserMapper;

@Service
@RequiredArgsConstructor
public class UserService
{
    private final UserRepository userRepository;
    private final UserFinder userFinder;
    private final AuthService authService;
    private final UserMapper mapper;

    @Transactional
    public UserResponse createUser(CreateUserRequest request)
    {
        if (userFinder.existsByName(request.name()))
        {
            throw new NameInUseException(request.name());
        }

        User user = userRepository.save(mapper.toEntity(request));
        return mapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(UserFilter filter, Long currentUserId, Pageable pageable)
    {
        authService.verifyAdminRole(currentUserId);

        Specification<User> specification = UserSpecification.withFilter(filter);

        return userFinder
            .getUsers(specification, pageable)
            .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id, Long currentUserId)
    {
        authService.verifyAdminRole(currentUserId);
        return mapper.toResponse(userFinder.getUser(id));
    }
}