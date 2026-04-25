package taskmanager.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taskmanager.task.TaskStatus;

import java.util.Map;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class TaskStatsController
{
    private final TaskStatsService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Map<TaskStatus, Long> getStats()
    {
        return service.getStats();
    }

    @PostMapping("/refresh")
    @PreAuthorize("hasRole('ADMIN')")
    public void refreshStats()
    {
        service.updateStats();
    }
}