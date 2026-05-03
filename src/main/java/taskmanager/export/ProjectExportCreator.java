package taskmanager.export;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import taskmanager.exception.ResourceNotAvailableException;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
public class ProjectExportCreator
{
    public ProjectExportResponse prepare(ReentrantLock lock, Long projectId)
    {
        final int maxAttempts = 3;
        for (int i =0; i < maxAttempts; i++)
        {
            try
            {
                Optional<ProjectExportResponse> export = tryGenerate(lock, projectId);
                if (export.isPresent()) return export.get();
                Thread.sleep(200);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Lock wait interrupted", e);
            }
        }

        throw new ResourceNotAvailableException("The export cannot be performed now. Please try again later.");
    }

    private Optional<ProjectExportResponse> tryGenerate(ReentrantLock lock, Long projectId) throws InterruptedException
    {
        if (lock.tryLock(1000, TimeUnit.MILLISECONDS))
        {
            try
            {
                return Optional.of(new ProjectExportResponse(projectId));
            }
            finally
            {
                lock.unlock();
            }
        }

        return Optional.empty();
    }
}