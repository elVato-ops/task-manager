package taskmanager.export;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class ProjectExportService
{
    private final ProjectExportLockManager lockManager;
    private final ProjectExportCreator exportCreator;

    // MOCKED EXPORT GENERATION
    public ProjectExportResponse exportData(Long projectId)
    {
        ReentrantLock lock = lockManager.obtainLock(projectId);
        return exportCreator.prepare(lock, projectId);
    }
}
