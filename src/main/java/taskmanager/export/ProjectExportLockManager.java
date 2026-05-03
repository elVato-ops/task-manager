package taskmanager.export;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class ProjectExportLockManager
{
    private final ConcurrentHashMap<Long, ReentrantLock> lockPerProjectId = new ConcurrentHashMap<>();

    public ReentrantLock obtainLock(Long projectId)
    {
        return lockPerProjectId.computeIfAbsent(projectId, k -> new ReentrantLock());
    }
}