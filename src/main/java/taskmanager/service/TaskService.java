package taskmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanager.repository.TaskRepository;

@Service
@Transactional
@AllArgsConstructor
public class TaskService
{
    private final TaskRepository taskRepository;
}
