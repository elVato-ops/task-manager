package taskmanager.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import taskmanager.exception.NotFoundException;

import static taskmanager.exception.ResourceType.USER;

@Component
@RequiredArgsConstructor
public class UserFinder
{
    private final UserRepository userRepository;

    public Page<User> getUsers(Specification<User> specification, Pageable pageable)
    {
        return userRepository
                .findAll(specification, pageable);
    }

    public User getUser(Long id)
    {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(id, USER));
    }

    public boolean existsByName(String name)
    {
        return userRepository
                .existsByName(name);
    }
}