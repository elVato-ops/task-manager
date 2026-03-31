package taskmanager.user;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import taskmanager.exception.NotFoundException;

import static taskmanager.exception.ResourceType.USER;

@Component
@AllArgsConstructor
public class UserFinder
{
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<User> getUsers(Specification<User> specification, Pageable pageable)
    {
        return userRepository
                .findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    public User getUser(Long id)
    {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(id, USER));
    }
}