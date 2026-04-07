package taskmanager.task;

import org.junit.jupiter.api.Test;
import taskmanager.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static taskmanager.TestConstants.*;
import static taskmanager.exception.ResourceType.TASK;

public class TaskTest
{
    @Test
    public void constructorTest()
    {
        ValidationException e = assertThrows(ValidationException.class, () -> new Task("", TASK_STATUS, project(), user()));
        assertEquals(TASK, e.getResource());
        assertEquals("Task name must not be empty", e.getMessage());

        e = assertThrows(ValidationException.class, () -> new Task(null, TASK_STATUS, project(), user()));
        assertEquals(TASK, e.getResource());
        assertEquals("Task name must not be empty", e.getMessage());

        e = assertThrows(ValidationException.class, () -> new Task("Name", null, project(), user()));
        assertEquals(TASK, e.getResource());
        assertEquals("Task status must not be null", e.getMessage());

        e = assertThrows(ValidationException.class, () -> new Task("Name", TASK_STATUS, null, user()));
        assertEquals(TASK, e.getResource());
        assertEquals("Task project must not be null", e.getMessage());

        Task task = new Task(TASK_NAME, TASK_STATUS, project(), user());
        assertEquals(TASK_NAME, task.getName());
        assertEquals(project().getName(), task.getProject().getName());
        assertEquals(user().getName(), task.getAssignee().getName());
        assertEquals(TASK_STATUS, task.getStatus());
    }
}