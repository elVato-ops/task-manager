package taskmanager.stats;

import org.springframework.stereotype.Component;
import taskmanager.task.TaskStatus;
import taskmanager.task.TaskStatusCount;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TaskStatsCache
{
    private volatile Map<TaskStatus, Long> cache = Map.of();

    public void updateStats(List<TaskStatusCount> tasks)
    {
        cache = tasks.stream()
                .collect(Collectors.toUnmodifiableMap(
                        TaskStatusCount::getStatus,
                        TaskStatusCount::getCount
                ));
    }

    public Map<TaskStatus, Long> getStats()
    {
        return cache;
    }
}