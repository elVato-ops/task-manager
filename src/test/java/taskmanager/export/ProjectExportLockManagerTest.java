package taskmanager.export;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static taskmanager.TestConstants.PROJECT_ID;

public class ProjectExportLockManagerTest
{
    @Test
    public void returnsLock_ifObtained()
    {
        //GIVEN
        ProjectExportLockManager manager = new ProjectExportLockManager();

        //WHEN
        ReentrantLock reentrantLock = manager.obtainLock(PROJECT_ID);

        //THEN
        assertNotNull(reentrantLock);
    }
}
