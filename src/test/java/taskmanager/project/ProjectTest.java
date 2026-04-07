package taskmanager.project;

import org.junit.jupiter.api.Test;
import taskmanager.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static taskmanager.TestConstants.PROJECT_NAME;
import static taskmanager.TestConstants.user;
import static taskmanager.exception.ResourceType.PROJECT;

public class ProjectTest
{
    @Test
    public void constructorTest()
    {
        ValidationException e = assertThrows(ValidationException.class, () -> new Project("", user()));
        assertEquals(PROJECT, e.getResource());
        assertEquals("Project name must not be empty", e.getMessage());

        e = assertThrows(ValidationException.class, () -> new Project(null, user()));
        assertEquals(PROJECT, e.getResource());
        assertEquals("Project name must not be empty", e.getMessage());

        e = assertThrows(ValidationException.class, () -> new Project("name", null));
        assertEquals(PROJECT, e.getResource());
        assertEquals("Project owner must not be null", e.getMessage());

        Project project = new Project(PROJECT_NAME, user());
        assertEquals(PROJECT_NAME, project.getName());
        assertEquals(user().getName(), project.getOwner().getName());
    }
}
