package taskmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanager.repository.UserRepository;

@Service
@AllArgsConstructor
@Transactional
public class UserService
{
    private final UserRepository userRepository;
}
