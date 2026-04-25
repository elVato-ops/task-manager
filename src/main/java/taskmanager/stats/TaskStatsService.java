package taskmanager.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanager.task.TaskRepository;
import taskmanager.task.TaskStatus;
import taskmanager.task.TaskStatusCount;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskStatsService
{
    private final TaskStatsCache taskStatsCache;
    private final TaskRepository taskRepository;

    public Map<TaskStatus, Long> getStats()
    {
        return taskStatsCache.getStats();
    }

    @Scheduled(fixedDelay = 30000)
    @Transactional(readOnly = true)
    public void updateStats()
    {
        List<TaskStatusCount> taskStatusCount = taskRepository.countByStatus();
        taskStatsCache.updateStats(taskStatusCount);
    }
}