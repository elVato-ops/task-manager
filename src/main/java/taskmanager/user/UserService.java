package taskmanager.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanager.auth.dto.AuthenticatedUser;
import taskmanager.exception.NameInUseException;
import taskmanager.user.dto.CreateUserRequest;
import taskmanager.user.dto.UserResponse;
import taskmanager.user.filter.UserFilter;
import taskmanager.user.specification.UserSpecification;
import taskmanager.utils.AccessGuard;
import taskmanager.utils.UserMapper;

import static taskmanager.exception.ResourceType.USER;

@Service
@RequiredArgsConstructor
public class UserService
{
    private final UserRepository userRepository;
    private final UserFinder userFinder;
    private final UserMapper mapper;
    private final AccessGuard accessGuard;

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
    public Page<UserResponse> getUsers(UserFilter filter, Pageable pageable)
    {
        Specification<User> specification = UserSpecification.withFilter(filter);

        return userFinder
            .getUsers(specification, pageable)
            .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id, AuthenticatedUser user)
    {
        accessGuard.verifyRights(user, id, USER, id);
        return mapper.toResponse(userFinder.getUser(id));
    }
}