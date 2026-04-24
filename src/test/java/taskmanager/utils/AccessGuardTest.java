package taskmanager.utils;

import org.junit.jupiter.api.Test;
import taskmanager.exception.ForbiddenAccessException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static taskmanager.TestConstants.*;
import static taskmanager.exception.ResourceType.PROJECT;

public class AccessGuardTest
{
    private final AccessGuard accessGuard = new AccessGuard();

    @Test
    public void verifyRights()
    {
        //WHEN
        accessGuard.verifyRights(authenticatedUser(), USER_ID, PROJECT, PROJECT_ID);
        accessGuard.verifyRights(authenticatedAdmin(), USER_ID, PROJECT, PROJECT_ID);

        ForbiddenAccessException exception = assertThrows(ForbiddenAccessException.class,
                () -> accessGuard.verifyRights(authenticatedUser(), OTHER_USER_ID, PROJECT, PROJECT_ID));

        //THEN
        assertEquals(USER_ID, exception.getRequesterId());
        assertEquals(PROJECT, exception.getResourceType());
        assertEquals(PROJECT_ID, exception.getResourceId());
    }
}
