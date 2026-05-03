package taskmanager.export;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import taskmanager.exception.ResourceNotAvailableException;

import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static taskmanager.TestConstants.PROJECT_ID;
import static taskmanager.TestConstants.projectExportResponse;

@ExtendWith(MockitoExtension.class)
public class ProjectExportServiceTest
{
    @InjectMocks
    private ProjectExportService projectExportService;

    @Mock
    private ProjectExportLockManager lockManager;

    @Mock
    private ProjectExportCreator exportCreator;

    @Test
    public void returnsExport_whenSuccess()
    {
        //GIVEN
        when(lockManager.obtainLock(PROJECT_ID)).thenReturn(new ReentrantLock());
        when(exportCreator.prepare(any(ReentrantLock.class), eq(PROJECT_ID))).thenReturn(projectExportResponse());

        //WHEN
        ProjectExportResponse exportResponse = projectExportService.exportData(PROJECT_ID);

        //THEN
        verify(lockManager, times(1)).obtainLock(PROJECT_ID);
        verifyNoMoreInteractions(lockManager);
        verify(exportCreator, times(1)).prepare(any(ReentrantLock.class), eq(PROJECT_ID));
        verifyNoMoreInteractions(exportCreator);

        assertEquals(projectExportResponse().projectId(), exportResponse.projectId());
    }

    @Test
    public void throwsResourceNotAvailableException_whenResourceNotAvailable()
    {
        //GIVEN
        when(lockManager.obtainLock(PROJECT_ID)).thenReturn(new ReentrantLock());
        when(exportCreator.prepare(any(ReentrantLock.class), eq(PROJECT_ID))).thenThrow(ResourceNotAvailableException.class);

        //WHEN
        assertThrows(ResourceNotAvailableException.class,
                () -> projectExportService.exportData(PROJECT_ID));

        //THEN
        verify(lockManager, times(1)).obtainLock(PROJECT_ID);
        verifyNoMoreInteractions(lockManager);
        verify(exportCreator, times(1)).prepare(any(ReentrantLock.class), eq(PROJECT_ID));
        verifyNoMoreInteractions(exportCreator);
    }
}
